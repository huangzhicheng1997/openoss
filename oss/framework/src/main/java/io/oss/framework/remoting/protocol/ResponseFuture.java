package io.oss.framework.remoting.protocol;

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

    private volatile boolean reset;

    public ResponseFuture() {
        this.startTime = System.currentTimeMillis();
    }

    public FileCommand get() throws InterruptedException {
        countDownLatch.await(1000, TimeUnit.SECONDS);
        if (reset) {
            throw new UploadResetException();
        }
        return fileCommandResp;
    }

    public static void onRest(Integer requestSeq) {
        ResponseFuture responseFuture = responseFutureTable.get(requestSeq);
        responseFuture.reset = true;
        responseFuture.countDownLatch.countDown();

    }

    public static void onComplete(Integer requestSeq, FileCommand fileCommandResp) {
        ResponseFuture responseFuture = responseFutureTable.get(requestSeq);
        responseFuture.fileCommandResp = fileCommandResp;
        responseFuture.countDownLatch.countDown();
    }

    public Boolean isInValid() {
        return System.currentTimeMillis() - startTime > 60000;
    }

    public static void putToTable(Integer requestSeq, ResponseFuture responseFuture) {
        responseFutureTable.put(requestSeq, responseFuture);
    }

}
