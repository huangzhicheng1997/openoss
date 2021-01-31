package io.oss.framework.remoting.listener;

import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.listener.event.ChannelInactiveEvent;
import io.oss.framework.remoting.listener.event.UserEventTriggerEvent;

/**
 * @author zhicheng
 * @date 2021-01-22 15:45
 */
public interface ChannelEventListener {

    public void onActive(ChannelActiveEvent evt);

    public void onInActive(ChannelInactiveEvent evt);

    public void onUserEventTriggered(UserEventTriggerEvent evt);
}
