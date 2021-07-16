package io.oss.kernel.support;

import io.netty.channel.SimpleChannelInboundHandler;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.ComponentInitializer;

/**
 * @Author zhicheng
 * @Date 2021/6/23 8:27 下午
 * @Version 1.0
 */
public abstract class PropertiesAvailableChannelInBindHandler<T> extends SimpleChannelInboundHandler<T> implements EnvironmentAware, ComponentInitializer {
    private IsolatedEnvironment environment;

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        this.environment = environment;
    }

    public String getProperties(String key) {
        return environment.getPrivateProperty(getEnvironmentName(), key);
    }


    protected abstract String getEnvironmentName();
}
