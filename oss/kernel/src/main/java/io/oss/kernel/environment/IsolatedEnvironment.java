package io.oss.kernel.environment;

import io.netty.util.internal.StringUtil;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * 可隔离的运行环境，各个模块获取系统配置的入口
 * <p>
 * 每个模块获取到的其他模块的环境都是副本，所以crud的操作不影响内核的运行。每个模块的
 * 可以通过{@link EnvironmentAware}获取环境
 * </p>
 *
 * @Author zhicheng
 * @Date 2021/4/10 4:15 下午
 * @Version 1.0
 */
public class IsolatedEnvironment implements Environment {

    private Map<String/*namespace*/, Environment> moduleEnvironments = new TreeMap<>();

    @Override
    public Properties getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Properties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProperty(String name, String value) {
        throw new UnsupportedOperationException();
    }

    public void registerNameSpace(NamedEnvironment namedEnvironment) {
        moduleEnvironments.put(namedEnvironment.getNameSpace(), namedEnvironment);
    }

    /**
     * 获取配置，以第一个为准
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }
        final String[] result = new String[1];
        moduleEnvironments.forEach((k, v) -> {
            result[0] = v.getProperties().getProperty(key);
        });
        return result[0];
    }

    /**
     * 以当前的命名空间的第一个配置为准
     *
     * @param nameSpace
     * @param key
     * @return
     */
    public String getPrivateProperty(String nameSpace, String key) {
        Environment environment = moduleEnvironments.get(nameSpace);
        if (null != environment) {
            return environment.getProperties().getProperty(key);
        }
        return null;
    }
}
