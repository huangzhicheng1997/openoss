package io.oss.kernel.support;

import io.netty.channel.ChannelHandlerContext;
import io.oss.util.exception.ExceptionCode;

/**
 * <p>
 * 不需要特殊处理的异常，只是返回一个状态码和报错信息
 * 注意只有在{@link ExceptionCode}中维护的异常，
 * 且在执行到此异常处理器前未进行拦截的情况下，才会被捕获。
 * </p>
 *
 * <p>
 * 此处理器必须位于倒数第二位，作为后备处理
 * </p>
 *
 * @Author zhicheng
 * @Date 2021/6/1 5:04 下午
 * @Version 1.0
 */
public class JustSendErrorMsgExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return null != ExceptionCode.match(ex.getClass());
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        ExceptionCode exceptionCode = ExceptionCode.match(ex.getClass());
        sendError(exceptionCode.getMsg(), exceptionCode, context.channel());
        return false;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE - 1;
    }
}
