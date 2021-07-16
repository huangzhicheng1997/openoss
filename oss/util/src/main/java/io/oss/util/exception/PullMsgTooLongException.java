package io.oss.util.exception;

/**
 * @Author zhicheng
 * @Date 2021/5/24 7:43 下午
 * @Version 1.0
 */
public class PullMsgTooLongException extends RuntimeException{
    public PullMsgTooLongException() {
    }

    public PullMsgTooLongException(String message) {
        super(message);
    }

    public PullMsgTooLongException(String message, Throwable cause) {
        super(message, cause);
    }

    public PullMsgTooLongException(Throwable cause) {
        super(cause);
    }

    public PullMsgTooLongException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
