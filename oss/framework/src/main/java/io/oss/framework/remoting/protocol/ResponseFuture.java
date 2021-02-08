package io.oss.framework.remoting.protocol;

import io.oss.util.exception.UploadRejectException;
import io.oss.util.exception.UploadResetException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseFuture {

    private static final Map<Integer/*requestSeq*/, ResponseFuture> responseFutureTable = new ConcurrentHashMap<>();

    private FileCommand fileCommandResp;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private final Long startTime;

    private volatile int exceptionCode = -1;

    public ResponseFuture() {
        this.startTime = System.currentTimeMillis();
    }

    public FileCommand get() throws InterruptedException {
        countDownLatch.await(1000, TimeUnit.SECONDS);
        switch (exceptionCode) {
            case ExceptionCode.RESET:
                throw new UploadResetException();
            case ExceptionCode.reject:
                throw new UploadRejectException("uploadReject because file is uploading!");
        }
        return fileCommandResp;
    }

    public static void onComplete(Integer requestSeq, FileCommand fileCommandResp) {
        ResponseFuture responseFuture = responseFutureTable.get(requestSeq);
        responseFuture.fileCommandResp = fileCommandResp;
        responseFuture.countDownLatch.countDown();
    }

    public static void onException(Integer requestSeq, Integer code) {
        ResponseFuture responseFuture = responseFutureTable.get(requestSeq);
        responseFuture.exceptionCode = code;
        responseFuture.countDownLatch.countDown();
    }

    public Boolean isInValid() {
        return System.currentTimeMillis() - startTime > 60000;
    }

    public static void putToTable(Integer requestSeq, ResponseFuture responseFuture) {
        responseFutureTable.put(requestSeq, responseFuture);
    }

}
