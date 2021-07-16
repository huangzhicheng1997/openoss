package io.oss.kernel.spi.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

/**
 * @Author zhicheng
 * @Date 2021/4/25 8:50 下午
 * @Version 1.0
 */
public class ServerSocketBindEvent implements ApplicationEvent {
    private ChannelHandlerContext ctx;
    private SocketAddress localAddress;
    private ChannelPromise promise;

    public ServerSocketBindEvent(ChannelHandlerContext ctx,
                                 SocketAddress localAddress,
                                 ChannelPromise promise) {
        this.ctx = ctx;
        this.localAddress = localAddress;
        this.promise = promise;
    }

    @Override
    public Object getSource() {
        return this;
    }


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public ChannelPromise getPromise() {
        return promise;
    }


}
