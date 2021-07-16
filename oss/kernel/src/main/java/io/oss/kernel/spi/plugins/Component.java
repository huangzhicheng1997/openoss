package io.oss.kernel.spi.plugins;

/**
 * @Author zhicheng
 * @Date 2021/4/27 8:25 下午
 * @Version 1.0
 */
public interface Component {

    /**
     * 默认组件名为类名，子类可以重写，重写的值返回null，则不注册为组件
     * 目的是如果此组件想在{@link ComponentFactory}中进行组件注册，
     * 那么就应该返回null。
     *
     * @return 组件名
     */
    default String getName() {
        return this.getClass().getName();
    }

}
