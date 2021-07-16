package io.oss.kernel.exception;

/**
 * @author zhicheng
 * @date 2021-04-29 16:15
 */
public class ComponentNotFindException extends RuntimeException{

    public ComponentNotFindException() {
    }

    public ComponentNotFindException(String message) {
        super(message);
    }

    public ComponentNotFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentNotFindException(Throwable cause) {
        super(cause);
    }

    public ComponentNotFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
