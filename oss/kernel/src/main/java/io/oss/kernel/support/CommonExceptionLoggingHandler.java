package io.oss.kernel.support;

import io.netty.channel.ChannelHandlerContext;
import io.oss.protocol.exception.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常日志记录
 *
 * @author zhicheng
 * @date 2021-05-10 17:39
 */
public class CommonExceptionLoggingHandler implements ChannelOuterExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean support(Throwable ex) {
        return true;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        if (ExceptionCode.match(ex.getClass()) == null) {
            logger.warn("log", ex);
        }
        return true;
    }
}
