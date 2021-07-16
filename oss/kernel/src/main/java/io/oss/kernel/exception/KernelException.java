package io.oss.kernel.exception;

/**
 * @author zhicheng
 * @date 2021-04-29 20:15
 */
public class KernelException extends RuntimeException {
    public KernelException() {
        super();
    }

    public KernelException(String message) {
        super(message);
    }

    public KernelException(String message, Throwable cause) {
        super(message, cause);
    }

    public KernelException(Throwable cause) {
        super(cause);
    }

    protected KernelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
