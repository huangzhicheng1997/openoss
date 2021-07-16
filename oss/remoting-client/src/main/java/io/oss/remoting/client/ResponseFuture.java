package io.oss.remoting.client;

import io.oss.util.Command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhicheng
 * @Date 2021/5/27 8:09 下午
 * @Version 1.0
 */
public class ResponseFuture {

    private volatile Command response;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private volatile boolean isCompleted;

    /**
     * 响应对应的请求序列号
     */
    private volatile Integer requestSeq;

    private static final Map<Integer/*seq*/, ResponseFuture> responseFutures = new ConcurrentHashMap<>();

    public ResponseFuture(Integer requestSeq) {
        this.requestSeq = requestSeq;
        responseFutures.put(requestSeq, this);
    }

    public boolean isComplete() {
        return isCompleted;
    }

    public Command getSync() throws InterruptedException {
        countDownLatch.await();
        return response;
    }

    public Command tryGet(long timeout) throws InterruptedException {
        countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        return response;
    }

    public Command getOnce() {
        return response;
    }

    public void put(Command response) {
        this.response = response;
        isCompleted = true;
        countDownLatch.countDown();
    }


    /**
     * 完成一次请求
     *
     * @param seq
     * @param response
     */
    public static void completeRequest(Integer seq, Command response) {
        ResponseFuture responseFuture = responseFutures.get(seq);
        if (responseFuture != null) {
            responseFuture.put(response);
        }
    }

}
