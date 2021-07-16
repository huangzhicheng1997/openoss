package io.oss.kernel.exception;

/**
 * @Author zhicheng
 * @Date 2021/5/25 4:08 下午
 * @Version 1.0
 */
public class UnknownException extends RuntimeException{
    public UnknownException() {
    }

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownException(Throwable cause) {
        super(cause);
    }

    public UnknownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
