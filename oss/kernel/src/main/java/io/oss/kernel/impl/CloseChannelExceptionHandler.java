package io.oss.kernel.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.oss.kernel.support.ChannelOuterExceptionHandler;

/**
 * @author zhicheng
 * @date 2021-05-11 17:06
 */
public class CloseChannelExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof DecoderException;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        context.channel().close();
        return false;
    }
}
