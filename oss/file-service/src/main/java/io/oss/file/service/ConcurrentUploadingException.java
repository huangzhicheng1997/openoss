package io.oss.file.service;

/**
 * @author zhicheng
 * @date 2021-05-21 14:52
 */
public class ConcurrentUploadingException extends RuntimeException {
    public ConcurrentUploadingException() {
    }

    public ConcurrentUploadingException(String message) {
        super(message);
    }

    public ConcurrentUploadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentUploadingException(Throwable cause) {
        super(cause);
    }

    public ConcurrentUploadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
