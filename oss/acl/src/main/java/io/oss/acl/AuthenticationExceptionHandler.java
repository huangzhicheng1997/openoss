package io.oss.acl;

import io.netty.channel.ChannelHandlerContext;
import io.oss.util.exception.AuthenticationException;
import io.oss.kernel.support.ChannelOuterExceptionHandler;
import io.oss.util.exception.ExceptionCode;

/**
 * @author zhicheng
 * @date 2021-05-13 18:07
 */
public class AuthenticationExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof AuthenticationException;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        sendError(ex.toString(), ExceptionCode.AUTH_FAILED, context.channel());
        return false;
    }
}
