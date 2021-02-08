package io.oss.file.upload;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.oss.framework.config.ServerConfiguration;
import io.oss.framework.remoting.handler.ChannelReadHandler;
import io.oss.framework.remoting.listener.AbstractChannelEventListener;
import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.listener.event.ChannelInactiveEvent;
import io.oss.framework.remoting.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author zhicheng
 * @date 2021-01-22 17:27
 */
@Component(name = "fileUploadProcessor")
public class FileUploadProcessor extends AbstractChannelEventListener implements ChannelReadHandler {

    private Map<Channel, Map<String/*fileName*/, TempFile>> fileTable = new ConcurrentHashMap<>();

    private ServerConfiguration serverConfiguration;

    private final Object mutex = new Object();

    private Logger logger = LoggerFactory.getLogger(FileUploadProcessor.class);

    public FileUploadProcessor(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    public void onActive(ChannelActiveEvent evt) {
        fileTable.put(evt.getCtx().channel(), new ConcurrentHashMap<>());
    }

    @Override
    public void onInActive(ChannelInactiveEvent evt) {
        //释放文件句柄
        Channel channel = evt.getCtx().channel();
        Map<String, TempFile> tempFileMap = fileTable.get(channel);
        if (null != tempFileMap) {
            tempFileMap.forEach((s, tempFile) -> {
                tempFile.closeChannel();
            });
        }
        fileTable.remove(channel);

    }

    @Override
    public boolean isMatch(Byte protocolType) {
        return RequestCode.UPLOAD_REQUEST_START == protocolType || RequestCode.UPLOAD_REQUEST_FILE == protocolType;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, FileCommand msg) {
        if (msg.getProtocolType() == RequestCode.UPLOAD_REQUEST_START) {
            prepareUploading(ctx.channel(), msg);
            handleUploadRequest(ctx, msg);
        } else {
            writeToTempFile(ctx.channel(), msg);
        }
    }

    /**
     * 检查是否已经有同名文件正在上传中，有的话拒绝上传
     * @param channel
     * @param msg
     */
    private void prepareUploading(Channel channel, FileCommand msg) {
        Map<String, TempFile> stringTempFileMap = fileTable.get(channel);
        if (stringTempFileMap != null) {
            TempFile file = stringTempFileMap.get(msg.getFileData().getFileName());
            if (file != null) {
                FileCommand fileCommand = FileCommandFactory.ResponseFactory.rejectResponse(msg.getRequestSeq());
                channel.writeAndFlush(fileCommand).addListener(future -> {
                    if (future.isSuccess()) {
                        channel.closeFuture();
                    }
                });
            }
        }

    }

    private void writeToTempFile(Channel channel, FileCommand msg) {
        FileData fileData = msg.getFileData();
        Map<String, TempFile> tempFileMap = fileTable.get(channel);
        TempFile tempFile = tempFileMap.get(msg.getFileData().getFileName());
        try {
            if (null == tempFile) {
                tempFile = new TempFile(fileData.getFileName(), serverConfiguration.getFileStorePath(), fileData.getFileSize());
                addTempFileTable(channel, tempFile);
            }
            tempFile.write(fileData.getFileBuffer());
            if (tempFile.isOver()) {
                tempFile.finishFile();
            }
            FileCommand fileCommand = FileCommandFactory.ResponseFactory.uploadFileResponse(msg.getRequestSeq(),
                    tempFile.getTempFileOffset());

            //上传完成关闭tcp连接
            final TempFile filePtr = tempFile;
            channel.writeAndFlush(fileCommand).addListener(future -> {
                if (future.isSuccess() && filePtr.isOver()) {
                    channel.closeFuture();
                }
            });


        } catch (Exception e) {
            logger.error("写入异常，开始重试", e);
            //异常发送reset
            if (null != tempFile) {
                tempFile.resetWriteOffset();
            }
            //防止文件
            sendReset(channel, msg);
        }


    }

    private void handleUploadRequest(ChannelHandlerContext ctx, FileCommand msg) {
        Channel channel = ctx.channel();
        FileData fileData = msg.getFileData();
        String fileName = fileData.getFileName();
        long fileSize = fileData.getFileSize();
        Map<String, TempFile> tempFileMap = fileTable.get(channel);
        try {
            if (!tempFileMap.containsKey(msg.getFileData().getFileName())) {
                TempFile tempFile = new TempFile(fileName, serverConfiguration.getFileStorePath(), fileSize);
                addTempFileTable(channel, tempFile);
                //如果获取的临时文件已经传完了,但还是临时文件因为能查到，说明重命名失败，重新finishFile()
                if (tempFile.isOver()) {
                    tempFile.finishFile();
                }
                doResponse(channel, msg, tempFile);
            } else {
                doResponse(channel, msg, tempFileMap.get(msg.getFileData().getFileName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传启动异常。。reset!!!", e);
            sendReset(channel, msg);
        }
    }

    private void addTempFileTable(Channel channel, TempFile tempFile) {
        Map<String, TempFile> tempFileMap = fileTable.get(channel);
        tempFileMap.put(tempFile.getOriginFileName(), tempFile);
    }

    private void sendReset(Channel channel, FileCommand msg) {
        FileCommand fileCommand = FileCommandFactory.ResponseFactory.uploadResetResponse(
                msg.getRequestSeq(), 0);
        channel.writeAndFlush(fileCommand);
    }


    private void doResponse(Channel channel, FileCommand msg, TempFile tempFile) {
        int requestSeq = msg.getRequestSeq();
        FileCommand fileCommand = FileCommandFactory.ResponseFactory.uploadStartResponse(requestSeq, tempFile.getTempFileOffset());
        channel.writeAndFlush(fileCommand);
    }


    @Override
    public ExecutorService taskExecutor() {
        return null;
    }
}
