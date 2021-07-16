package io.oss.util.exception;

public class OSSServerException extends RuntimeException{
    public OSSServerException() {
    }

    public OSSServerException(String message) {
        super(message);
    }

    public OSSServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OSSServerException(Throwable cause) {
        super(cause);
    }

    public OSSServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
