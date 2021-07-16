package io.oss.remoting.client;

/**
 * @Author zhicheng
 * @Date 2021/5/29 7:03 下午
 * @Version 1.0
 */
public class RemotingClientException extends RuntimeException{
    public RemotingClientException() {
    }

    public RemotingClientException(String message) {
        super(message);
    }

    public RemotingClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingClientException(Throwable cause) {
        super(cause);
    }

    public RemotingClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
