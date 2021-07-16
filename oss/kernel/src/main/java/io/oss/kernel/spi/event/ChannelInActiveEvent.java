package io.oss.kernel.spi.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author zhicheng
 * @Date 2021/4/28 9:06 下午
 * @Version 1.0
 */
public class ChannelInActiveEvent implements ApplicationEvent {

    private ChannelHandlerContext channelHandlerContext;

    public ChannelInActiveEvent(ChannelHandlerContext ctx) {
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
