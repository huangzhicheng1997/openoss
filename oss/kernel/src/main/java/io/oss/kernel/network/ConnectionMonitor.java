package io.oss.kernel.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.oss.kernel.support.ApplicationEventMultiCaster;
import io.oss.kernel.spi.event.*;

import java.net.SocketAddress;

/**
 * @Author zhicheng
 * @Date 2021/4/25 8:17 下午
 * @Version 1.0
 */
public class ConnectionMonitor extends ChannelDuplexHandler {

    private ApplicationEventMultiCaster applicationEventMultiCaster;

    public ConnectionMonitor(ApplicationEventMultiCaster applicationEventMultiCaster) {
        this.applicationEventMultiCaster = applicationEventMultiCaster;
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        applicationEventMultiCaster.publishEvent(new ServerSocketBindEvent(ctx, localAddress, promise));
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        applicationEventMultiCaster.publishEvent(new ChannelClosedEvent(ctx, promise));
        super.close(ctx, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        applicationEventMultiCaster.publishEvent(new ChannelActiveEvent(ctx));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        applicationEventMultiCaster.publishEvent(new ChannelInActiveEvent(ctx));
        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        applicationEventMultiCaster.publishEvent(new userEventTriggerEvent(ctx, evt));
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }


}
