package io.oss.kernel.spi.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author zhicheng
 * @Date 2021/4/28 9:07 下午
 * @Version 1.0
 */
public class userEventTriggerEvent implements ApplicationEvent {

    private ChannelHandlerContext channelHandlerContext;

    private Object evt;

    public userEventTriggerEvent(ChannelHandlerContext ctx, Object evt) {
        this.evt = evt;
        this.channelHandlerContext = ctx;
    }

    @Override
    public Object getSource() {
        return this;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public Object getEvt() {
        return evt;
    }
}
