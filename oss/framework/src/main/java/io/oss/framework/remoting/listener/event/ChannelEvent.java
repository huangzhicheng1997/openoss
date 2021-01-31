package io.oss.framework.remoting.listener.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhicheng
 * @date 2021-01-22 15:57
 */
public abstract class ChannelEvent {
    private ChannelHandlerContext ctx;

    public ChannelEvent(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
