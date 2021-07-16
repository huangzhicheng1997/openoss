package io.oss.file.service;

import io.oss.kernel.Inject;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.kernel.support.AutoDependenciesInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author zhicheng
 * @date 2021-05-18 15:30
 */
public class MappedFileCleaner implements AutoDependenciesInjector, WheelTask, EnvironmentAware {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Inject
    private PullFileServiceWrapper pullFileServiceWrapper;

    private Long mappedFileCleanFrequency;

    @Override
    public void execute() {
        pullFileServiceWrapper.getPullFileService().cleanResource();
    }

    @Override
    public Long delayMillSeconds() {
        return mappedFileCleanFrequency;
    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        mappedFileCleanFrequency = Long.parseLong(environment.getPrivateProperty(FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME,
                "mapped.file.clean.frequency"));
    }
}
