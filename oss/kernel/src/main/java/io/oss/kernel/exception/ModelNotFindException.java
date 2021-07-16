package io.oss.kernel.exception;

/**
 * @Author zhicheng
 * @Date 2021/5/24 8:50 下午
 * @Version 1.0
 */
public class ModelNotFindException extends RuntimeException {
    public ModelNotFindException() {
    }

    public ModelNotFindException(String message) {
        super(message);
    }

    public ModelNotFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelNotFindException(Throwable cause) {
        super(cause);
    }

    public ModelNotFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
