package io.oss.util;

import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @Author zhicheng
 * @Date 2021/4/12 7:55 下午
 * @Version 1.0
 */
public interface CodecHelp {

    void codecComponentInject(ChannelHandlerInitializer channelHandlerInitializer);

    void setCodecEventExecutor(EventExecutorGroup eventExecutorGroup);

}
