package io.oss.framework.core;

import io.oss.framework.remoting.handler.ChannelReadHandler;
import io.oss.framework.remoting.listener.ChannelEventListener;
import io.oss.framework.remoting.protocol.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhicheng
 * @date 2021-01-25 17:42
 */
public class InitializationContext {


    private Map<String, ChannelReadHandler> channelReadHandlerSingletons = new ConcurrentHashMap<>();

    private Map<String, ChannelEventListener> channelEventListenerSingletons = new ConcurrentHashMap<>();

    public void registerHandler(ChannelReadHandler... channelReadHandlers) {
        for (ChannelReadHandler channelReadHandler : channelReadHandlers) {
            Class<? extends ChannelReadHandler> clazz = channelReadHandler.getClass();
            Component annotation = clazz.getAnnotation(Component.class);
            String name = null;
            if (null == annotation) {
                name = clazz.getName();
            } else {
                name = annotation.name();
            }
            channelReadHandlerSingletons.put(name, channelReadHandler);
        }
    }

    public void registerEventListener(ChannelEventListener... channelEventListeners) {
        for (ChannelEventListener channelEventListener : channelEventListeners) {
            Class<? extends ChannelEventListener> clazz = channelEventListener.getClass();
            Component annotation = clazz.getAnnotation(Component.class);
            String name = null;
            if (null == annotation) {
                name = clazz.getName();
            } else {
                name = annotation.name();
            }
            channelEventListenerSingletons.put(name, channelEventListener);
        }
    }

    public ChannelReadHandler getHandler(String name) {
        return channelReadHandlerSingletons.get(name);
    }

    public ChannelEventListener getListener(String name) {
        return channelEventListenerSingletons.get(name);
    }

    private static class Instance {
        private static InitializationContext context = new InitializationContext();
    }

    public static InitializationContext getInstance() {
        return Instance.context;
    }
}
