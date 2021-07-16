package io.oss.kernel.spi.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author zhicheng
 * @Date 2021/4/28 9:04 下午
 * @Version 1.0
 */
public class ChannelActiveEvent implements ApplicationEvent {
    private ChannelHandlerContext channelHandlerContext;

    public ChannelActiveEvent(ChannelHandlerContext ctx) {
        this.channelHandlerContext = ctx;
    }

    @Override
    public Object getSource() {
        return this;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
