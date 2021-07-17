package io.oss.kernel.spi.plugins;

import io.netty.util.concurrent.EventExecutorGroup;
import io.oss.protocol.ChannelHandlerInitializer;

/**
 * @author zhicheng
 * @date 2021-05-06 16:04
 */
public interface ChannelHandlerInitializerAware extends Component {

    void aware(ChannelHandlerInitializer channelHandlerInitializer, EventExecutorGroup eventExecutorGroup);

}
