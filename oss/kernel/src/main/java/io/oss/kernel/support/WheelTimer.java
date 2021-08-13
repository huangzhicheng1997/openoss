package io.oss.kernel.support;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.oss.kernel.spi.event.NettyServerStartEvent;
import io.oss.kernel.spi.listener.ApplicationListener;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.spi.plugins.WheelTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 时间轮算法进行单线程定时资源清理
 *
 * @author zhicheng
 * @date 2021-05-07 16:52
 */
public class WheelTimer implements FindDependenciesComponent, ApplicationListener<NettyServerStartEvent> {

    private List<WheelTask> wheelTasks = new ArrayList<>();

    private ChannelOuterExceptionHandlerChain channelOuterExceptionHandlerChain;

    @Override
    public void setComponentDependencies(Map<String, Component> componentMap) {
        componentMap.values().forEach(component -> {
            if (component instanceof WheelTask) {
                wheelTasks.add((WheelTask) component);
            }
        });
        this.channelOuterExceptionHandlerChain = (ChannelOuterExceptionHandlerChain) componentMap.get("io.oss.kernel.support.ChannelOuterExceptionHandlerChain");
    }

    @Override
    public void listen(NettyServerStartEvent applicationEvent) {
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
        wheelTasks.forEach(task -> {
            Long delayMillSeconds = task.delayMillSeconds();
            if (null == delayMillSeconds) {
                return;
            }
            hashedWheelTimer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    try {
                        task.execute();
                    } catch (Exception e) {
                        channelOuterExceptionHandlerChain.handle(null, e);
                    }
                    timeout.timer().newTimeout(this, delayMillSeconds, TimeUnit.MILLISECONDS);
                }
            }, delayMillSeconds, TimeUnit.MILLISECONDS);
        });
        hashedWheelTimer.start();
    }

    @Override
    public double getOrder() {
        return 0;
    }
}
