package io.oss.file.service;

import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.ComponentInitializer;

import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-05-18 11:39
 */
public class PullFileServiceWrapper implements EnvironmentAware, ComponentInitializer {

    private PullFileService pullFileService;

    private boolean bufferCacheOpen = false;

    private Long buffCacheTotalSize;

    private Long bufferCachePerFileThreshold;

    private Long mappedFileMaxFreeTime;

    private Integer bufferCacheLoadThreshold;

    @Override
    public void afterInit() {
        DirectLRUBufferCache directLruBufferCache = new DirectLRUBufferCache(true, buffCacheTotalSize);
        pullFileService = new PullFileService(directLruBufferCache);
        pullFileService.setMappedFileMaxFreeTime(mappedFileMaxFreeTime);
        pullFileService.setAddCacheThreshold(bufferCacheLoadThreshold);
        pullFileService.setPerBufferCacheLimitCapacity(bufferCachePerFileThreshold);
    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        this.bufferCacheOpen =
                Boolean.parseBoolean(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "buffer.cache.open"));
        this.buffCacheTotalSize =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "buffer.cache.total.size"));
        this.bufferCachePerFileThreshold =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "buffer.cache.per.file.threshold"));
        this.mappedFileMaxFreeTime =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "mapped.file.max.free.time"));
        this.bufferCacheLoadThreshold =
                Integer.parseInt(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "buffer.cache.load.threshold"));


    }

    public PullFileService getPullFileService() {
        return pullFileService;
    }


    public long getFileLength(String filePath) {
        return pullFileService.getFileLength(filePath);
    }


    public ByteBuffer pullPartOfFile(String filePath, Long position, Integer length) {
        return pullFileService.pullPartOfFile(filePath, position, length);
    }
}
