package io.oss.file.service;

import java.io.IOException;

/**
 * @author zhicheng
 * @date 2021-05-17 11:10
 */
public class ConcurrentCreateMappedFileException extends RuntimeException {
    public ConcurrentCreateMappedFileException() {
    }

    public ConcurrentCreateMappedFileException(String message) {
        super(message);
    }

    public ConcurrentCreateMappedFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentCreateMappedFileException(Throwable cause) {
        super(cause);
    }

}
