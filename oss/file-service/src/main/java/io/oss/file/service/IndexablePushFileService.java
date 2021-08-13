package io.oss.file.service;

import io.netty.channel.Channel;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.protocol.Command;
import io.oss.protocol.facade.PBUploadingCommandLifeCycle;
import io.oss.protocol.facade.UploadingCommandLifeCycle;
import io.oss.protocol.http.HttpChannelRecord;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * 可索引的文件推送服务
 *
 * @Author zhicheng
 * @Date 2021/5/24 5:20 下午
 * @Version 1.0
 */
public class IndexablePushFileService implements WheelTask, EnvironmentAware, ComponentInitializer {

    private final PushFileService pushFileService = new PushFileService();

    private UploadingCommandLifeCycle uploadingCommandLifeCycle = new PBUploadingCommandLifeCycle();

    /**
     * 文件描述符最大可空闲时间，超过此时间未使用则会释放描述符
     */
    private static long uploadingFileFreeTime;

    private static final Integer MAX_INDEX_SLOT_LENGTH = HashIndex.MAXIMUM_CAPACITY;

    //todo 文件路径转化hook


    /**
     * 获取下一次文件上传的位点
     *
     * @param filePath 文件路径
     * @param channel  {@link Channel}
     * @return {@link UploadingCommandLifeCycle}
     * @throws IOException
     */
    public Command getNextPushOffset(String filePath, Channel channel) throws IOException {
        long nextUploadOffset = pushFileService.getUploadOffset(filePath) + 1;
        return dispatchChannelResp(
                () -> {
                    // 暂时不实现http上传
                    throw new UnsupportedOperationException("not support http uploading");
                },
                () -> uploadingCommandLifeCycle.obtainNextUploadOffsetAck(nextUploadOffset, channel),
                channel);
    }


    public Command pushBuffer(String filePath, ByteBuffer byteBuffer, Long pushOffset, Long fullFileLength, Channel channel) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            throw new IllegalFileOptionException("not support directory");
        }
        HashIndex hashIndex = new HashIndex(MAX_INDEX_SLOT_LENGTH, file.getParent());
        hashIndex.
        pushFileService.pushBuffer(filePath, byteBuffer, pushOffset);

        hashIndex.append(file, fullFileLength);

        return dispatchChannelResp(
                () -> {
                    throw new UnsupportedOperationException("not support http uploading")
                },
                () -> {
                    uploadingCommandLifeCycle.pushAck()
                }, channel);

    }

    @Deprecated
    public Long getUploadOffset(String filePath) throws IOException {
        return pushFileService.getUploadOffset(filePath);
    }

    @Deprecated
    public void pushBuffer(String filePath, ByteBuffer byteBuffer, long position) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            throw new IllegalFileOptionException("not support directory");
        }
        pushFileService.pushBuffer(filePath, byteBuffer, position);
    }

    @Deprecated
    public void finish(String filePath) throws IOException {
        pushFileService.finish(filePath);
    }

    @Override
    public void execute() {
        pushFileService.daemonClear();
    }

    @Override
    public Long delayMillSeconds() {
        return 60 * 1000L;
    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        uploadingFileFreeTime =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "uploading.file.free.time"));
    }

    @Override
    public void afterInit() {
        this.pushFileService.setUploadingFileFreeTime(uploadingFileFreeTime);
    }

    private Command dispatchChannelResp(Supplier<Command> httpSupplier, Supplier<Command> pbSupplier, Channel channel) {
        if (HttpChannelRecord.isHttpChannel(channel)) {
            return httpSupplier.get();
        } else {
            return pbSupplier.get();
        }
    }
}
