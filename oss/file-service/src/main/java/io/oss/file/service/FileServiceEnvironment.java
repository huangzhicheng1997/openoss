package io.oss.file.service;

import io.oss.kernel.environment.NamedEnvironment;

/**
 * @author zhicheng
 * @date 2021-05-18 11:50
 */
public class FileServiceEnvironment extends NamedEnvironment {

    public static final String FILE_SERVICE_ENVIRONMENT_NAME = "file_SERVICE";

    public FileServiceEnvironment() {
        super(FILE_SERVICE_ENVIRONMENT_NAME);
    }
}
