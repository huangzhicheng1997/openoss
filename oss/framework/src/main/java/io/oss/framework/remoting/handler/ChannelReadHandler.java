package io.oss.framework.remoting.handler;

import io.oss.framework.remoting.protocol.FileCommand;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * @author zhicheng
 * @date 2021-01-22 16:51
 */
public interface ChannelReadHandler {

    public boolean isMatch(Byte protocolType);

    public void channelRead(ChannelHandlerContext ctx, FileCommand msg);

    public ExecutorService taskExecutor();
}
