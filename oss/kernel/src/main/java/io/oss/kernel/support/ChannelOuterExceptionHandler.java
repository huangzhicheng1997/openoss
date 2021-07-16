package io.oss.kernel.support;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.oss.kernel.spi.plugins.Component;
import io.oss.util.*;
import io.oss.util.exception.ExceptionCode;

/**
 * @author zhicheng
 * @date 2021-05-08 15:49
 */
public interface ChannelOuterExceptionHandler extends Component {

    /**
     * 是否支持处理此异常
     *
     * @param ex
     * @return
     */
    boolean support(Throwable ex);

    /**
     * 执行
     *
     * @return true继续执行，false停止
     */
    boolean invoke(ChannelHandlerContext context, Throwable ex);


    default int order() {
        return 0;
    }

    default void sendError(String errorMsg, ExceptionCode code, Channel channel) {
        RemotingCall.writeAndFlush(channel, CommandBuilder.errorMsgCommand(channel, code, errorMsg));
    }


}
