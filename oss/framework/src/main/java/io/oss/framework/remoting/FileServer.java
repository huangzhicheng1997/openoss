package io.oss.framework.remoting;

import io.oss.framework.config.NettyConfiguration;
import io.oss.framework.config.ServerConfiguration;
import io.oss.framework.remoting.handler.ChannelConnectionManager;
import io.oss.framework.remoting.handler.DispatcherChannelHandler;
import io.oss.framework.remoting.codec.FileEncoder;
import io.oss.framework.remoting.codec.FileDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.oss.util.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author zhicheng
 * @date 2021-01-18 14:19
 */
public class FileServer {

    private Logger logger = LoggerFactory.getLogger(FileServer.class);
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup acceptor;
    private EventLoopGroup ioWorker;
    private DefaultEventExecutorGroup handlerWorker;
    private NettyConfiguration nettyConfiguration;
    private ServerConfiguration serverConfiguration;
    private Class channelClass;
    private DispatcherChannelHandler dispatcherChannelHandler;
    private ChannelConnectionManager channelConnectionManager;


    public FileServer(NettyConfiguration nettyConfiguration, DispatcherChannelHandler dispatcherChannelHandler,
                      ChannelConnectionManager channelConnectionManager, ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
        this.channelConnectionManager = channelConnectionManager;
        this.dispatcherChannelHandler = dispatcherChannelHandler;
        this.nettyConfiguration = nettyConfiguration;
        serverBootstrap = new ServerBootstrap();
        this.handlerWorker = new DefaultEventExecutorGroup(nettyConfiguration.getHandlerWorks());
        if (PlatformUtil.isLinux()) {
            acceptor = new EpollEventLoopGroup(nettyConfiguration.getAcceptors());
            ioWorker = new EpollEventLoopGroup(nettyConfiguration.getIoWorkers());
            channelClass = EpollServerSocketChannel.class;
        } else {
            acceptor = new NioEventLoopGroup(nettyConfiguration.getAcceptors());
            ioWorker = new NioEventLoopGroup(nettyConfiguration.getIoWorkers());
            channelClass = NioServerSocketChannel.class;
        }
        init();
    }

    private void init() {
        serverBootstrap.group(acceptor, ioWorker).channel(channelClass)
                .option(ChannelOption.SO_BACKLOG, nettyConfiguration.getSo_backlog())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_SNDBUF, nettyConfiguration.getSo_sendBuffer())
                .childOption(ChannelOption.SO_RCVBUF, nettyConfiguration.getSo_receiveBuffer())
                .childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .localAddress(new InetSocketAddress(serverConfiguration.getPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handlerWorker, new FileEncoder())
                        .addLast(handlerWorker, new FileDecoder(nettyConfiguration.getMaxFrameLength(), 0, Integer.BYTES))
                        .addLast(handlerWorker, new IdleStateHandler(0, 0, 10))
                        .addLast(handlerWorker, channelConnectionManager)
                        .addLast(handlerWorker, dispatcherChannelHandler);
            }
        });

    }

    public void start() {
        serverBootstrap.bind().addListener(future -> {
            if (future.isSuccess()) {
               /* logger.info("server start");*/
                System.out.println("server start");
            }
        });
    }
}
