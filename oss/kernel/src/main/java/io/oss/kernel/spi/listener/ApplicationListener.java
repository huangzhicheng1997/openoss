package io.oss.kernel.spi.listener;

import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.Ordered;
import io.oss.kernel.spi.event.ApplicationEvent;

/**
 * @Author zhicheng
 * @Date 2021/4/10 6:51 下午
 * @Version 1.0
 */
public interface ApplicationListener<T extends ApplicationEvent> extends Ordered, Component {

    public void listen(T applicationEvent);

}
