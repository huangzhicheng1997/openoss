package io.oss.framework.remoting.listener.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhicheng
 * @date 2021-01-22 16:01
 */
public class ChannelInactiveEvent extends ChannelEvent {

    public ChannelInactiveEvent(ChannelHandlerContext ctx) {
        super(ctx);
    }
}
