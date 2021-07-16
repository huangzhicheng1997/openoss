package io.oss.kernel.exception;

/**
 * @author zhicheng
 * @date 2021-05-06 9:56
 */
public class NettyOptionPopulateException extends RuntimeException {
    public NettyOptionPopulateException() {
        super();
    }

    public NettyOptionPopulateException(String message) {
        super(message);
    }

    public NettyOptionPopulateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyOptionPopulateException(Throwable cause) {
        super(cause);
    }

    protected NettyOptionPopulateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
