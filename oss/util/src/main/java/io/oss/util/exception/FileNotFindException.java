package io.oss.util.exception;

/**
 * @Author zhicheng
 * @Date 2021/6/8 8:40 下午
 * @Version 1.0
 */
public class FileNotFindException extends RuntimeException {
    public FileNotFindException() {
        super();
    }

    public FileNotFindException(String message) {
        super(message);
    }

    public FileNotFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFindException(Throwable cause) {
        super(cause);
    }

    protected FileNotFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
