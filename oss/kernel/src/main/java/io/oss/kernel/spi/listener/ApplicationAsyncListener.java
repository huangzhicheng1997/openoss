package io.oss.kernel.spi.listener;

import io.oss.kernel.spi.event.ApplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 异步监听程序辅助类，详细逻辑参考{@link io.oss.kernel.support.ApplicationEventMultiCaster}
 *
 * @Author zhicheng
 * @Date 2021/4/25 8:21 下午
 * @Version 1.0
 */
public abstract class ApplicationAsyncListener<T extends ApplicationEvent> implements ApplicationListener<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ThreadPoolExecutor threadPoolExecutor;

    private ExecutorService defaultExecutor = Executors.newFixedThreadPool(8);

    /**
     * half标识
     */
    private boolean isHalfAsync = false;

    private Consumer<CompletionPromise> futureListener;

    private CompletionPromise completionPromise = new CompletionPromise();


    @Override
    public void listen(T applicationEvent) {
        if (null == threadPoolExecutor) {
            threadPoolExecutor = (ThreadPoolExecutor) defaultExecutor;
        }

        threadPoolExecutor.execute(() -> {
            try {
                listen0(applicationEvent);
                completionPromise.setSuccess(true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                completionPromise.setDone(true);
                if (null != futureListener) {
                    futureListener.accept(completionPromise);
                }
            }
        });
    }

    protected abstract void listen0(T applicationEvent);

    /**
     * 设置isHalfAsync
     */
    protected abstract void setHalfAsync();

    /**
     * 是否为半异步
     */
    public boolean isHalfAsync() {
        return this.isHalfAsync;
    }

    /**
     * 设置监听程序的线程池，
     * 默认会由{@link io.oss.kernel.support.ApplicationEventMultiCaster}去设置
     * 子类也可重写，使用自己的线程池，以达到逻辑上形成流水线模式，更加精确的控制线程资源
     *
     * @param threadPoolExecutor 线程池
     */
    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public double getOrder() {
        return Double.MIN_VALUE;
    }
}
