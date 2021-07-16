package io.oss.kernel.support;

import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.spi.plugins.Ordered;
import io.oss.kernel.spi.event.ApplicationEvent;
import io.oss.kernel.spi.listener.ApplicationAsyncListener;
import io.oss.kernel.spi.listener.ApplicationListener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 事件广播器,不主动注册到容器
 *
 * @Author zhicheng
 * @Date 2021/4/10 7:09 下午
 * @Version 1.0
 */
public class ApplicationEventMultiCaster implements Component {

    private List<ApplicationListener> applicationListeners;

    private final List<ApplicationListener> asyncListeners = new ArrayList<>();

    private final List<ApplicationListener> halfListeners = new ArrayList<>();

    private final List<ApplicationListener> syncListeners = new ArrayList<>();

    private ThreadPoolExecutor threadPoolExecutor;


    public ApplicationEventMultiCaster() {
    }

    public void setApplicationListeners(List<ApplicationListener> applicationListeners) {
        this.applicationListeners = applicationListeners;
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * 事件发布
     *
     * @param applicationEvent {@link ApplicationEvent}
     */
    public void publishEvent(ApplicationEvent applicationEvent) {

        syncListeners.forEach(applicationListener -> {
            if (checkIsTarget(applicationListener, applicationEvent)) {
                applicationListener.listen(applicationEvent);
            }
        });
    }

    /**
     * 查找事件是否匹配此监听器
     *
     * @param applicationListener applicationListener
     * @param applicationEvent    applicationEvent
     * @return
     */
    private boolean checkIsTarget(ApplicationListener applicationListener, ApplicationEvent applicationEvent) {
        try {
            try {
                Method listen = applicationListener.getClass().getDeclaredMethod("listen", applicationEvent.getClass());
            } catch (NoSuchMethodException e) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void init() {
        //排序
        this.applicationListeners.sort(Comparator.comparingDouble(Ordered::getOrder));
        this.syncListeners.addAll(this.applicationListeners);
    }
}
