package io.oss.file.upload;

import java.util.concurrent.*;

public class UploadResultFuture implements Future<UploadProgress> {

    private boolean isCanceled;

    private boolean isDone;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private UploadProgress uploadProgress;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCanceled;
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public UploadProgress get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return uploadProgress;
    }

    @Override
    public UploadProgress get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        countDownLatch.await(timeout, unit);
        return uploadProgress;
    }

    public void onFinish(UploadProgress uploadProgress) {
        this.uploadProgress = uploadProgress;
        countDownLatch.countDown();
    }
}
