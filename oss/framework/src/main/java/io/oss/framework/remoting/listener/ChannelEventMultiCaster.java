package io.oss.framework.remoting.listener;

import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.listener.event.ChannelEvent;
import io.oss.framework.remoting.listener.event.ChannelInactiveEvent;
import io.oss.framework.remoting.listener.event.UserEventTriggerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author zhicheng
 * @date 2021-01-22 15:44
 */
public class ChannelEventMultiCaster {

    /*private Logger logger = LoggerFactory.getLogger(ChannelEventMultiCaster.class);*/

    private List<ChannelEventListener> channelEventListeners = new ArrayList<>();

    private ExecutorService executorService;

    public ChannelEventMultiCaster(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void register(ChannelEventListener channelEventListener) {
        channelEventListeners.add(channelEventListener);
    }

    public void publish(ChannelEvent event) {
        executorService.submit(() -> {
            channelEventListeners.forEach(channelEventListener -> {
                try {
                    if (event instanceof ChannelActiveEvent) {
                        channelEventListener.onActive((ChannelActiveEvent) event);
                    }
                    if (event instanceof ChannelInactiveEvent) {
                        channelEventListener.onInActive((ChannelInactiveEvent) event);
                    }
                    if (event instanceof UserEventTriggerEvent) {
                        channelEventListener.onUserEventTriggered((UserEventTriggerEvent) event);
                    }
                } catch (Exception e) {
                  /*  logger.error(e.getMessage());*/
                    e.printStackTrace();
                }
            });
        });
    }

}
