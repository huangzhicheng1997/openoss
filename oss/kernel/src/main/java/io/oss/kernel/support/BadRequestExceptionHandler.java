package io.oss.kernel.support;

import io.netty.channel.ChannelHandlerContext;
import io.oss.util.exception.BadRequestException;
import io.oss.util.exception.ExceptionCode;

/**
 * @Author zhicheng
 * @Date 2021/6/8 4:04 下午
 * @Version 1.0
 */
public class BadRequestExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof BadRequestException;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        sendError(ex.toString(), ExceptionCode.BAD_REQUEST, context.channel());
        return false;
    }
}
