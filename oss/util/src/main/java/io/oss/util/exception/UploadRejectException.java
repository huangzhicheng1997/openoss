package io.oss.util.exception;

/**
 * @author zhicheng
 * @date 2021-02-05 16:25
 */
public class UploadRejectException extends RuntimeException{
    public UploadRejectException() {
    }

    public UploadRejectException(String message) {
        super(message);
    }
}
