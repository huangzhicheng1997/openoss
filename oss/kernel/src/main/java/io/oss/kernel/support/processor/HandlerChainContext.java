package io.oss.kernel.support.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhicheng
 * @date 2021-04-29 16:46
 */
public class HandlerChainContext {

    public static final String CHANNEL_CONTEXT = "channelContext";

    private ConcurrentMap<Object, Object> attrs = new ConcurrentHashMap<>();

    public void addAttr(Object key, Object value) {
        attrs.put(key, value);
    }

    public Object getAttr(Object key) {
        return attrs.get(key);
    }

}
