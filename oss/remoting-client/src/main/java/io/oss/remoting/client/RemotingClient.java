package io.oss.remoting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.oss.protocol.*;
import io.oss.util.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author zhicheng
 * @Date 2021/5/26 10:43 上午
 * @Version 1.0
 */
public class RemotingClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bootstrap bootstrap = new Bootstrap();


    private EventLoopGroup eventLoopGroup;

    private DefaultEventExecutorGroup eventExecutors;

    private Class<? extends SocketChannel> socketChannel;

    private CodecHelp codecHelp;

    private Map<String, ChannelFuture> channelTable = new ConcurrentHashMap<>();

    private Lock monitor = new ReentrantLock();

    public RemotingClient(CommandFactory commandFactory, CodecHelp codecHelp) {
        CommandFactoryHolder.addCommandFactory(commandFactory);
        this.codecHelp = codecHelp;
        init();
    }

    public void init() {
        if (PlatformUtil.isLinux()) {
            eventLoopGroup = new EpollEventLoopGroup(1);
            socketChannel = EpollSocketChannel.class;
        } else {
            eventLoopGroup = new NioEventLoopGroup(1);
            socketChannel = NioSocketChannel.class;
        }
        this.eventExecutors = new DefaultEventExecutorGroup(4);

        ChannelResponseAwakeHandler channelResponseAwakeHandler = new ChannelResponseAwakeHandler();

        bootstrap.group(eventLoopGroup).channel(socketChannel)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        ChannelHandlerInitializer channelHandlerInitializer = new ChannelHandlerInitializer(pipeline);
                        codecHelp.codecComponentInject(channelHandlerInitializer);
                        codecHelp.setCodecEventExecutor(eventExecutors);
                        channelHandlerInitializer.addLast(channelResponseAwakeHandler);
                        channelHandlerInitializer.addLast(new ChannelDuplexHandler() {
                            @Override
                            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                                super.close(ctx, promise);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                logger.debug(cause.toString());
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                logger.debug("channel closed");
                            }
                        });
                    }
                });

    }

    public ResponseFuture request(Command command, SocketAddress socketAddress) throws InterruptedException {
        ChannelFuture future = getChannel(socketAddress);
        future.await(3000, TimeUnit.MILLISECONDS);
        future.channel().writeAndFlush(command);
        return new ResponseFuture(command.getHeader().seq());
    }

    private ChannelFuture getChannel(SocketAddress address) {
        ChannelFuture channelFuture = channelTable.get(address.toString());
        boolean isNeedCreateChannel;
        if (null == channelFuture) {
            isNeedCreateChannel = true;
        } else {
            //三次握手完了，或者socket从队列中被取出注册到eventLoop中
            if (channelFuture.channel().isActive() || channelFuture.channel().isRegistered()) {
                isNeedCreateChannel = false;
            } else {
                isNeedCreateChannel = true;
                channelTable.remove(address.toString());
            }
        }
        if (isNeedCreateChannel) {
            monitor.lock();
            try {
                channelFuture = channelTable.get(address.toString());
                if (channelFuture == null) {
                    channelFuture = bootstrap.connect(address);
                    channelTable.put(address.toString(), channelFuture);
                }
            } finally {
                monitor.unlock();
            }
        }
        return channelFuture;
    }

}
