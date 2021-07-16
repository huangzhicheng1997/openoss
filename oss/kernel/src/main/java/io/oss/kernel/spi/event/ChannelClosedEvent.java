package io.oss.kernel.spi.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @Author zhicheng
 * @Date 2021/4/28 9:02 下午
 * @Version 1.0
 */
public class ChannelClosedEvent implements ApplicationEvent {
    private ChannelHandlerContext channelHandlerContext;
    private ChannelPromise channelPromise;

    public ChannelClosedEvent(ChannelHandlerContext ctx, ChannelPromise promise) {
        this.channelHandlerContext = ctx;
        this.channelPromise = promise;
    }


    @Override
    public Object getSource() {
        return this;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public ChannelPromise getChannelPromise() {
        return channelPromise;
    }
}
