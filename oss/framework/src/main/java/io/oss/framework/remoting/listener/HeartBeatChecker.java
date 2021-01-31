package io.oss.framework.remoting.listener;

import io.oss.framework.remoting.listener.event.UserEventTriggerEvent;
import io.oss.framework.remoting.protocol.Component;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhicheng
 * @date 2021-01-22 16:09
 */
@Component(name ="heartBeatChecker" )
public class HeartBeatChecker extends AbstractChannelEventListener {

    private Map<String/*addr*/, AtomicInteger/*times*/> channelInvalidNumber = new ConcurrentHashMap<>();

    @Override
    public void onUserEventTriggered(UserEventTriggerEvent evt) {
        if (evt.getEvt() instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt.getEvt();
            if (idleStateEvent.state().equals(IdleState.ALL_IDLE)) {
                Channel channel = evt.getCtx().channel();
                if (channelInvalidNumber.containsKey(channel.remoteAddress().toString())) {
                    int times = channelInvalidNumber.get(channel.remoteAddress().toString()).incrementAndGet();
                    if (times >= 3) {
                        SocketChannelTableInfo.removeChannelFromTable(channel);
                        channel.close();
                    }
                } else {
                    channelInvalidNumber.put(channel.remoteAddress().toString(), new AtomicInteger(1));
                }
            }
        }
    }
}
