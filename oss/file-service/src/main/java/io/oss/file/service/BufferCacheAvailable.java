package io.oss.file.service;

import io.oss.kernel.Inject;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.kernel.support.AutoDependenciesInjector;

import java.util.Map;

/**
 * @author zhicheng
 * @date 2021-05-18 16:06
 */
public class BufferCacheAvailable implements AutoDependenciesInjector, WheelTask, EnvironmentAware {

    @Inject
    private PullFileServiceWrapper pullFileServiceWrapper;

    private Long bufferCacheCheckFrequency;

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        bufferCacheCheckFrequency =
                Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME, "buffer.cache.check.frequency"));

    }

    @Override
    public void execute() {
        pullFileServiceWrapper.getPullFileService().smartAddBufferCache();
    }

    @Override
    public Long delayMillSeconds() {
        return bufferCacheCheckFrequency;
    }
}
