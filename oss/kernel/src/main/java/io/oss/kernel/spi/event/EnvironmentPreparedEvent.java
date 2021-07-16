package io.oss.kernel.spi.event;

import io.oss.kernel.environment.IsolatedEnvironment;

/**
 * @Author zhicheng
 * @Date 2021/4/10 6:53 下午
 * @Version 1.0
 */
public class EnvironmentPreparedEvent implements ApplicationEvent {

    private IsolatedEnvironment isolatedEnvironment;

    public EnvironmentPreparedEvent(IsolatedEnvironment isolatedEnvironment) {
        this.isolatedEnvironment = isolatedEnvironment;
    }

    @Override
    public Object getSource() {
        return this.isolatedEnvironment;
    }
}
