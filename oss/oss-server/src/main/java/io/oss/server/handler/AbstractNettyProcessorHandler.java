package io.oss.server.handler;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.kernel.support.processor.NettyProcessor;
import io.oss.protocol.BodyMsgExtension;
import io.oss.protocol.Command;

/**
 * @Author zhicheng
 * @Date 2021/5/24 8:23 下午
 * @Version 1.0
 */
public abstract class AbstractNettyProcessorHandler implements NettyProcessor {

    @Override
    public Command handle(Command request, HandlerChainContext context) {
        String msg = request.getBody().resp();
        Gson gson = new Gson();
        BodyMsgExtension bodyMsgExtension = gson.fromJson(msg, BodyMsgExtension.class);
        return handle(request, bodyMsgExtension, context);
    }


    protected Channel getChannel(HandlerChainContext chainContext) {
        ChannelHandlerContext channelHandlerContext = (ChannelHandlerContext) chainContext.getAttr(HandlerChainContext.CHANNEL_CONTEXT);
        return channelHandlerContext.channel();
    }

    protected abstract Command handle(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context);
}
