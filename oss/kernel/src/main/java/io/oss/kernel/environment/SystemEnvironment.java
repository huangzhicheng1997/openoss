package io.oss.kernel.environment;

import io.netty.util.internal.SystemPropertyUtil;

import java.util.Properties;

/**
 * @Author zhicheng
 * @Date 2021/4/20 8:41 下午
 * @Version 1.0
 */
public class SystemEnvironment extends NamedEnvironment {
    public static final String SYSTEM = "system";

    public SystemEnvironment() {
        super(SYSTEM);
    }

    @Override
    public Properties getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProperty(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Properties properties) {
        throw new UnsupportedOperationException();
    }

    public String getSystemProperty(String name) {
        return SystemPropertyUtil.get(name);
    }

}
