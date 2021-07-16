package io.oss.kernel.support;

import io.netty.channel.ChannelHandlerContext;
import io.oss.util.exception.ExceptionCode;

/**
 * @Author zhicheng
 * @Date 2021/5/25 3:13 下午
 * @Version 1.0
 */
public class UncaughtExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return true;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        sendError(ex.toString(), ExceptionCode.UN_CATCH_EXCEPTION, context.channel());
        return false;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }
}
