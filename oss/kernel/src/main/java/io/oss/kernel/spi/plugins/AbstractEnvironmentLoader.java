package io.oss.kernel.spi.plugins;

import io.oss.kernel.environment.NamedEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author zhicheng
 * @Date 2021/4/10 6:00 下午
 * @Version 1.0
 */
public abstract class AbstractEnvironmentLoader implements EnvironmentLoader {

    private String propertiesLocation;

    public AbstractEnvironmentLoader(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }

    @Override
    public NamedEnvironment loadEnvironment() {
        try {
            Properties properties = new Properties();
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(propertiesLocation);
            //todo 从命令行中获取 配置路径
            properties.load(resourceAsStream);
            return buildEnvironment(properties);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String getNameSpace() {
        return propertiesLocation;
    }

    protected abstract NamedEnvironment buildEnvironment(Properties properties);
}
