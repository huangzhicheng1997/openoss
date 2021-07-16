package io.oss.kernel.spi.plugins;

import java.util.Map;

/**
 * 内核默认组件名为组件类中进行定义，如果想创建多个同一个类的实例组件，可以借助此类实现
 *
 * @author zhicheng
 * @date 2021-04-30 14:54
 */
public interface ComponentFactory extends Component {

    Map<String, Component> getComponents();
}
