package io.oss.kernel.spi.plugins;

/**
 * 对内核插件进行初始化
 *
 * @Author zhicheng
 * @Date 2021/4/27 7:47 下午
 * @Version 1.0
 */
public interface ComponentInitializer extends Component{

    void afterInit();

}
