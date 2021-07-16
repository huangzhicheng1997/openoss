package io.oss.kernel.support;

import io.netty.channel.ChannelOption;
import io.oss.kernel.core.NettyServerBooster;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.event.ComponentsReadyEvent;
import io.oss.kernel.spi.event.EnvironmentPreparedEvent;
import io.oss.kernel.spi.event.NettyOptionsEffectedEvent;
import io.oss.kernel.spi.event.NettyServerStartEvent;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.spi.plugins.NettyOptionConfigAware;

import java.util.List;
import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/4/10 7:19 下午
 * @Version 1.0
 */
public class DefaultApplicationLifeCycleManager implements ApplicationLifeCycleManager, Component {

    private ApplicationEventMultiCaster eventMultiCaster;
    private List<Component> components;

    /**
     * 对应的bit位 为1则通知过了，为0则没通知过,没通知过则进行通知
     */
    private int state = 0;

    public DefaultApplicationLifeCycleManager() {
    }

    public DefaultApplicationLifeCycleManager(ApplicationEventMultiCaster eventMultiCaster, List<Component> components) {
        this.eventMultiCaster = eventMultiCaster;
        this.components = components;
    }


    @Override
    public void afterEnvironmentPrepared(IsolatedEnvironment environment) {
        if (checkState(0)) {
            components.forEach(component -> {
                if (component instanceof EnvironmentAware) {
                    ((EnvironmentAware) component).setEnvironment(environment);
                }
            });
            eventMultiCaster.publishEvent(new EnvironmentPreparedEvent(environment));
            updateState();
        }
    }

    @Override
    public void afterPreparePlugins() {
        if (checkState(1)) {
            components.forEach(component -> {
                if (component instanceof ComponentInitializer) {
                    ((ComponentInitializer) component).afterInit();
                }
            });
            eventMultiCaster.publishEvent(new ComponentsReadyEvent());
            updateState();
        }
    }


    @Override
    public void beforeNettyOptionEffect(Map<ChannelOption, Object> workerOptions, Map<ChannelOption, Object> bossOptions) {
        if (checkState(2)) {
            components.forEach(component -> {
                if (component instanceof NettyOptionConfigAware) {
                    ((NettyOptionConfigAware) component).aware(workerOptions, bossOptions);
                }
            });
            updateState();
        }
    }

    @Override
    public void afterNettyOptionEffect() {
        if (checkState(3)) {
            eventMultiCaster.publishEvent(new NettyOptionsEffectedEvent());
            updateState();
        }
    }

    @Override
    public void afterNettyServerStartSuccess(NettyServerBooster nettyServerBooster) {
        if (checkState(4)) {
            eventMultiCaster.publishEvent(new NettyServerStartEvent(nettyServerBooster));
            updateState();
        }
    }

    private void updateState() {
        state = state << 1 | 1;
    }

    private boolean checkState(int offset) {
        return (state & (1 << offset)) == 0;
    }


    @Override
    public String getName() {
        return null;
    }
}
