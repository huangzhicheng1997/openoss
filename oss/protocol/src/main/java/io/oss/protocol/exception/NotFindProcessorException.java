package io.oss.protocol.exception;

/**
 * @Author zhicheng
 * @Date 2021/6/1 4:15 下午
 * @Version 1.0
 */
public class NotFindProcessorException extends RuntimeException {
    public NotFindProcessorException() {
        super();
    }

    public NotFindProcessorException(String message) {
        super(message);
    }

    public NotFindProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFindProcessorException(Throwable cause) {
        super(cause);
    }

    protected NotFindProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
