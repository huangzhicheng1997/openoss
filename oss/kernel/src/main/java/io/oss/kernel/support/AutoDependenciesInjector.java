package io.oss.kernel.support;

import io.netty.util.internal.StringUtil;
import io.oss.kernel.Inject;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/6/23 8:54 下午
 * @Version 1.0
 */
public interface AutoDependenciesInjector extends FindDependenciesComponent {


    @Override
    default void setComponentDependencies(Map<String, Component> componentMap) {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        Arrays.stream(declaredFields).forEach(field -> {
            field.setAccessible(true);
            Inject inject = field.getAnnotation(Inject.class);
            if (null != inject) {
                try {
                    field.set(this, getValue(field, inject, componentMap));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    default Object getValue(Field field, Inject inject, Map<String, Component> componentMap) {
        Class<?> type = field.getType();
        if (!StringUtil.isNullOrEmpty(inject.componentName())) {
            return componentMap.get(inject.componentName());
        } else {
            return componentMap.get(type.getName());
        }
    }
}
