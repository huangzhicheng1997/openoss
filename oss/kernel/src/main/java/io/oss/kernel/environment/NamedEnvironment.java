package io.oss.kernel.environment;


import java.util.Properties;

/**
 * 附带命名空间的环境参数
 *
 * @Author zhicheng
 * @Date 2021/4/10 4:27 下午
 * @Version 1.0
 */
public abstract class NamedEnvironment implements Environment {

    private String nameSpace;

    private Properties properties = new Properties();

    /**
     * 子类用无参构造器，覆盖此构造器，来定义当前模块的配置所属的命名空间 例如：{@link KernelEnvironment}
     *
     * @param nameSpace
     */
    public NamedEnvironment(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    @Override
    public void putAll(Properties properties) {
        this.properties.putAll(properties);
    }
}
