package io.oss.remoting.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.oss.protocol.Command;

/**
 * @Author zhicheng
 * @Date 2021/5/27 8:29 下午
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class ChannelResponseAwakeHandler extends SimpleChannelInboundHandler<Command> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command response) throws Exception {
        Integer seq = response.getHeader().seq();
        ResponseFuture.completeRequest(seq, response);
    }
}
