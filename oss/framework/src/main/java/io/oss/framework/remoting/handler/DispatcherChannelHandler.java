package io.oss.framework.remoting.handler;

import io.oss.framework.remoting.protocol.FileCommand;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author zhicheng
 * @date 2021-01-22 16:40
 */
@ChannelHandler.Sharable
public class DispatcherChannelHandler extends SimpleChannelInboundHandler<FileCommand> {

    private List<ChannelReadHandler> channelReadHandlers = new CopyOnWriteArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileCommand msg) throws Exception {
        byte protocolType = msg.getProtocolType();

        List<ChannelReadHandler> handlers = channelReadHandlers.stream()
                .filter(channelReadHandler -> channelReadHandler.isMatch(protocolType))
                .collect(Collectors.toList());

        if (handlers.size() > 1) {
            throw new RuntimeException("冲突的ChannelReadHandler");
        }
        ChannelReadHandler channelReadHandler = handlers.get(0);
        ExecutorService executorService = channelReadHandler.taskExecutor();
        if (executorService == null) {
            channelReadHandler.channelRead(ctx, msg);
        } else {
            executorService.submit(() -> {
                channelReadHandler.channelRead(ctx, msg);
            });
        }

    }

    public void addLast(ChannelReadHandler channelReadHandler) {
        channelReadHandlers.add(channelReadHandler);
    }
}