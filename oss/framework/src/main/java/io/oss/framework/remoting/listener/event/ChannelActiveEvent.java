package io.oss.framework.remoting.listener.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhicheng
 * @date 2021-01-22 15:57
 */
public class ChannelActiveEvent extends ChannelEvent{

    public ChannelActiveEvent(ChannelHandlerContext ctx) {
        super(ctx);
    }

}
