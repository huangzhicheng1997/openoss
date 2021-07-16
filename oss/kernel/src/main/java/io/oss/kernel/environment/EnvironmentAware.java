package io.oss.kernel.environment;

/**
 * @Author zhicheng
 * @Date 2021/4/10 4:45 下午
 * @Version 1.0
 */
public interface EnvironmentAware {

    void setEnvironment(IsolatedEnvironment environment);
}
