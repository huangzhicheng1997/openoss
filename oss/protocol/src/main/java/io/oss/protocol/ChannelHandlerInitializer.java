package io.oss.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @Author zhicheng
 * @Date 2021/4/12 7:57 下午
 * @Version 1.0
 */
public class ChannelHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ChannelPipeline channelPipeline;

    public ChannelHandlerInitializer(ChannelPipeline channelPipeline) {
        this.channelPipeline = channelPipeline;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        this.channelPipeline = ch.pipeline();
    }

    public void addLast(ChannelHandler channelHandler) {
        channelPipeline.addLast(channelHandler);
    }

    public void addLast(EventExecutorGroup eventExecutors, ChannelHandler channelHandler) {
        channelPipeline.addLast(eventExecutors, channelHandler);
    }

    public ChannelPipeline getChannelPipeline() {
        return channelPipeline;
    }
}
