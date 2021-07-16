package io.oss.file.service;

/**
 * @Author zhicheng
 * @Date 2021/7/12 7:59 下午
 * @Version 1.0
 */
public class IllegalFileOptionException extends RuntimeException {
    public IllegalFileOptionException() {
        super();
    }

    public IllegalFileOptionException(String message) {
        super(message);
    }

    public IllegalFileOptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalFileOptionException(Throwable cause) {
        super(cause);
    }

    protected IllegalFileOptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
