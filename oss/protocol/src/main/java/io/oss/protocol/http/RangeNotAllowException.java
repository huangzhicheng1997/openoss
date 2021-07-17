package io.oss.protocol.http;

/**
 * @Author zhicheng
 * @Date 2021/6/4 2:52 下午
 * @Version 1.0
 */
public class RangeNotAllowException extends RuntimeException {
    public RangeNotAllowException() {
    }

    public RangeNotAllowException(String message) {
        super(message);
    }

    public RangeNotAllowException(String message, Throwable cause) {
        super(message, cause);
    }

    public RangeNotAllowException(Throwable cause) {
        super(cause);
    }

    public RangeNotAllowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
