package io.oss.kernel.spi.plugins;

import io.oss.kernel.environment.NamedEnvironment;

/**
 * @Author zhicheng
 * @Date 2021/4/10 5:58 下午
 * @Version 1.0
 */
public interface EnvironmentLoader extends Component {

    /**
     * 加载系统配置
     *
     * @return {@link NamedEnvironment}
     */
    NamedEnvironment loadEnvironment();

    /**
     * 获取配置所属的命名空间
     *
     * @return 命名空间
     */
    String getNameSpace();
}
