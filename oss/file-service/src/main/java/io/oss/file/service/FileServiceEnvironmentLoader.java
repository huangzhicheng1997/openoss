package io.oss.file.service;

import io.oss.kernel.environment.NamedEnvironment;
import io.oss.kernel.spi.plugins.AbstractEnvironmentLoader;

import java.util.Properties;

/**
 * @author zhicheng
 * @date 2021-05-18 11:51
 */
public class FileServiceEnvironmentLoader extends AbstractEnvironmentLoader {

    public FileServiceEnvironmentLoader() {
        super("fileService.properties");
    }

    @Override
    public String getNameSpace() {
        return FileServiceEnvironment.FILE_SERVICE_ENVIRONMENT_NAME;
    }

    @Override
    protected NamedEnvironment buildEnvironment(Properties properties) {
        FileServiceEnvironment fileServiceEnvironment = new FileServiceEnvironment();
        fileServiceEnvironment.putAll(properties);
        return fileServiceEnvironment;
    }
}
