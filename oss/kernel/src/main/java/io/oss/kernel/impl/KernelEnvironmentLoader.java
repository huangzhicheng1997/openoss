package io.oss.kernel.impl;

import io.oss.kernel.environment.KernelEnvironment;
import io.oss.kernel.environment.NamedEnvironment;
import io.oss.kernel.spi.plugins.AbstractEnvironmentLoader;
import io.oss.kernel.util.PlatformUtil;

import java.util.Properties;

/**
 * @Author zhicheng
 * @Date 2021/4/10 6:36 下午
 * @Version 1.0
 */
public class KernelEnvironmentLoader extends AbstractEnvironmentLoader {

    public KernelEnvironmentLoader() {
        super("kernel.properties");

    }

    @Override
    protected NamedEnvironment buildEnvironment(Properties properties) {
        KernelEnvironment kernelEnvironment = new KernelEnvironment();
        kernelEnvironment.putAll(properties);
        if (PlatformUtil.isLinux()) {
            kernelEnvironment.addProperty(KernelEnvironment.PLATFORM_TYPE, "linux");
        } else {
            kernelEnvironment.addProperty(KernelEnvironment.PLATFORM_TYPE, "other");
        }
        return kernelEnvironment;
    }
}
