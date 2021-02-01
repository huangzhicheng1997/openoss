package io.oss.file.upload;

import io.oss.util.exception.UploadClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UploadResultFuture {

    private volatile boolean isOK;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private UploadProgress uploadProgress;

    private List<GenericFutureListener> genericFutureListeners = new ArrayList<>();

    private volatile boolean isDone;

    public UploadResultFuture(UploadProgress uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public boolean isOK() {
        return this.isOK;
    }

    public UploadProgress get() throws InterruptedException {
        if (isDone) {
            throw new UploadClientException(new IllegalStateException("upload task is Running"));
        }
        countDownLatch.await();
        return uploadProgress;
    }

    public UploadProgress get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (isDone) {
            throw new UploadClientException(new IllegalStateException("upload task is Running"));
        }
        countDownLatch.await(timeout, unit);
        return uploadProgress;
    }


    public void onFinish() {
        this.isOK = true;
        countDownLatch.countDown();
        runListener();
    }

    public void onProcessing() {
        this.isDone = true;
        runListener();
    }

    public UploadProgress getUploadProgress() {
        return this.uploadProgress;
    }

    public void addListener(GenericFutureListener... futureListeners) {
        if (isDone) {
            throw new UploadClientException(new IllegalStateException("upload task is Running"));
        }
        genericFutureListeners.addAll(Arrays.asList(futureListeners));
    }

    private void runListener() {
        if (null != genericFutureListeners) {
            genericFutureListeners.forEach(futureListener -> {
                futureListener.listen(this);
            });
        }
    }

    public interface GenericFutureListener {
        void listen(UploadResultFuture future);
    }
}
