package io.oss.kernel.spi.listener;

/**
 * @Author zhicheng
 * @Date 2021/4/28 8:13 下午
 * @Version 1.0
 */
public class CompletionPromise {

    private volatile boolean isSuccess = false;

    private volatile boolean isDone = false;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

}
