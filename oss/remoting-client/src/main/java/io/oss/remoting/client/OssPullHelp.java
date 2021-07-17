package io.oss.remoting.client;

import io.netty.util.internal.StringUtil;
import io.oss.protocol.BodyFactory;
import io.oss.protocol.BodyMsgExtension;
import io.oss.protocol.Command;
import io.oss.protocol.CommandBuilder;
import io.oss.protocol.protobuf.PBCommandFactory;
import io.oss.protocol.protobuf.ProtobufCodecHelp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/5/29 6:50 下午
 * @Version 1.0
 */
public class OssPullHelp {

    private static final RemotingClient remotingClient = new RemotingClient(new PBCommandFactory(), new ProtobufCodecHelp());

    private static final String URL_FILE_LENGTH = "/oss/download/getFileLength";

    private static final String URL_PULL_FILE = "/oss/download/pull";

    private String accessToken = StringUtil.EMPTY_STRING;

    private final SocketAddress socketAddress;

    /**
     * 远程文件绝对路径
     */
    private final String remotingFilePath;

    /**
     * 下载到具体的本地文件绝对路径
     */
    private String localFilePath;

    private RandomAccessFile randomAccessFile;

    /**
     * 是否创建本地目标文件
     */
    private boolean createLocalFile;


    public OssPullHelp(String accessToken, SocketAddress socketAddress, String remotingFilePath, String localFilePath) {
        this.accessToken = accessToken;
        this.socketAddress = socketAddress;
        this.remotingFilePath = remotingFilePath;
        this.localFilePath = localFilePath;
        this.createLocalFile = true;
    }

    public OssPullHelp(String accessToken, SocketAddress socketAddress, String remotingFilePath) {
        this.accessToken = accessToken;
        this.socketAddress = socketAddress;
        this.remotingFilePath = remotingFilePath;
        this.createLocalFile = false;
    }

    /**
     * 打开文件
     *
     * @param createLocalFile true 创建本地文件 false不创建
     * @throws IOException
     */
    public synchronized void open(boolean createLocalFile) throws IOException {
        if (createLocalFile) {
            File file = new File(localFilePath);
            int i = 0;
            String temp = this.localFilePath;
            //重复文件，进行末尾加数字标识
            while (file.exists()) {
                String pre = this.localFilePath.substring(0, this.localFilePath.lastIndexOf("."));
                String after = "(" + ++i + ")" + this.localFilePath.substring(this.localFilePath.lastIndexOf("."));
                temp = pre + after;
                file = new File(temp);
            }
            if (file.isDirectory()) {
                throw new RemotingClientException("localFilePath can not be a directory!");
            }
            //检查目录
            String directory = temp.substring(0, temp.lastIndexOf(File.separator));
            File directoryFile = new File(directory);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }

            file.createNewFile();
            randomAccessFile = new RandomAccessFile(file, "rw");
        }
    }


    public long getRemotingFileSize() throws InterruptedException {
        Command command = CommandBuilder
                .commonRequest(URL_FILE_LENGTH, accessToken,
                        BodyFactory.getInstance().getFileLength(remotingFilePath));
        Command result = remotingClient.request(command, socketAddress).getSync();
        BodyMsgExtension msgExtension = resultHandle(result);
        if (msgExtension == null) {
            throw new RemotingClientException("unknown error");
        }
        return msgExtension.getFileLength();
    }

    public ByteBuffer pullPartOfFile(long position, int length) throws InterruptedException {
        Command command = CommandBuilder
                .commonRequest(URL_PULL_FILE, accessToken,
                        BodyFactory.getInstance().pullPartOfFile(remotingFilePath, position, length));
        Command result = remotingClient.request(command, socketAddress).getSync();
        resultHandle(result);
        return result.getBody().buffer();
    }

    public void writeToLocalFile(ByteBuffer buffer) throws IOException {
        if (createLocalFile) {
            randomAccessFile.getChannel().write(buffer);
            randomAccessFile.getFD().sync();
        }
    }


    public void close() throws IOException {
        if (createLocalFile) {
            randomAccessFile.close();
        }
    }


    private BodyMsgExtension resultHandle(Command result) {
        BodyMsgExtension msgExtension = BodyMsgExtension.fromJson(result.getBody().resp());

        if (null != msgExtension && null != msgExtension.getErrorMsg()) {
            throw new RemotingClientException(msgExtension.getErrorMsg());
        }
        return msgExtension;
    }
}
