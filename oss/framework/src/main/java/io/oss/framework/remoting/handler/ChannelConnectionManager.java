package io.oss.framework.remoting.handler;

import io.oss.framework.remoting.listener.ChannelEventMultiCaster;
import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.listener.event.ChannelInactiveEvent;
import io.oss.framework.remoting.listener.event.UserEventTriggerEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhicheng
 * @date 2021-01-22 15:38
 */
@ChannelHandler.Sharable
public class ChannelConnectionManager extends ChannelDuplexHandler {

    private Logger logger = LoggerFactory.getLogger(ChannelConnectionManager.class);

    private ChannelEventMultiCaster channelEventMultiCaster;

    public ChannelConnectionManager(ChannelEventMultiCaster channelEventMultiCaster) {
        this.channelEventMultiCaster = channelEventMultiCaster;
    }

    public ChannelConnectionManager() {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel　registered form " + ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel　unregistered form " + ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null != channelEventMultiCaster) {
            channelEventMultiCaster.publish(new ChannelActiveEvent(ctx));
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (null != channelEventMultiCaster) {
            channelEventMultiCaster.publish(new ChannelInactiveEvent(ctx));
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (null != channelEventMultiCaster) {
            channelEventMultiCaster.publish(new UserEventTriggerEvent(ctx, evt));
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
