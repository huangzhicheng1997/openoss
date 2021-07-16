package io.oss.kernel.spi.plugins;

import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/4/28 9:13 下午
 * @Version 1.0
 */
public interface FindDependenciesComponent extends Component{

    /**
     * 注册到 dependencies列表
     */
    void setComponentDependencies(Map<String,Component> componentMap);
}
