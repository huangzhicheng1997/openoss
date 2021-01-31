package io.oss.framework.remoting.listener.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhicheng
 * @date 2021-01-22 16:01
 */
public class UserEventTriggerEvent extends ChannelEvent {
    private Object evt;

    public UserEventTriggerEvent(ChannelHandlerContext ctx, Object evt) {
        super(ctx);
        this.evt = evt;
    }

    public Object getEvt() {
        return evt;
    }

    public void setEvt(Object evt) {
        this.evt = evt;
    }
}
