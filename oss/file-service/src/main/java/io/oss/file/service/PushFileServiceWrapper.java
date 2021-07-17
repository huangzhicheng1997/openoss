package io.oss.file.service;

import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.spi.plugins.WheelTask;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/5/24 5:20 下午
 * @Version 1.0
 */
public class PushFileServiceWrapper implements WheelTask, EnvironmentAware, ComponentInitializer {

    private final PushFileService pushFileService = new PushFileService();


    private static long uploadingFileFreeTime;

    public Long getUploadOffset(String filePath) throws IOException {
        return pushFileService.getUploadOffset(filePath);
    }


    public void pushBuffer(String filePath, ByteBuffer byteBuffer, long position) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            throw new IllegalFileOptionException("not support directory");
        }
       /* file.getParent() + File.separator + HashIndex.INDEX_NAME*/
        pushFileService.pushBuffer(filePath, byteBuffer, position);
    }


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
        this.uploadingFileFreeTime =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "uploading.file.free.time"));
    }

    @Override
    public void afterInit() {
        this.pushFileService.setUploadingFileFreeTime(uploadingFileFreeTime);
    }
}
