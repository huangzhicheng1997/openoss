package io.oss.framework.remoting.listener;

import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.listener.event.ChannelInactiveEvent;
import io.oss.framework.remoting.listener.event.UserEventTriggerEvent;

/**
 * @author zhicheng
 * @date 2021-01-22 15:49
 */
public class AbstractChannelEventListener implements ChannelEventListener {
    @Override
    public void onActive(ChannelActiveEvent evt) {

    }

    @Override
    public void onInActive(ChannelInactiveEvent evt) {

    }

    @Override
    public void onUserEventTriggered(UserEventTriggerEvent evt) {

    }
}
