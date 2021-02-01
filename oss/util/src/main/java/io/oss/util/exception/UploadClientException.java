package io.oss.util.exception;

/**
 * @author zhicheng
 * @date 2021-02-01 15:17
 */
public class UploadClientException extends RuntimeException {
    public UploadClientException(String message) {
        super(message);
    }

    public UploadClientException(Throwable cause) {
        super(cause);
    }
}
