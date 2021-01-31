package io.oss.framework.remoting;

import com.sun.javafx.PlatformUtil;
import io.oss.framework.config.ClientConfiguration;
import io.oss.framework.config.NettyConfiguration;
import io.oss.framework.remoting.handler.ChannelConnectionManager;
import io.oss.framework.remoting.listener.SocketChannelTableInfo;
import io.oss.framework.remoting.protocol.RequestCode;
import io.oss.framework.remoting.protocol.ResponseFuture;
import io.oss.framework.remoting.codec.FileEncoder;
import io.oss.framework.remoting.protocol.FileCommand;
import io.oss.framework.remoting.codec.FileDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhicheng
 * @date 2021-01-22 18:12
 */
public class FileClient {

    private Bootstrap bootstrap;
    private EventLoopGroup ioWorker;
    private DefaultEventExecutorGroup handlerWorker;
    private NettyConfiguration nettyConfiguration;
    private Class channelClass;
    private ChannelConnectionManager channelConnectionManager;
    private ClientConfiguration clientConfiguration;
    private Lock lock = new ReentrantLock();


    public FileClient(NettyConfiguration nettyConfiguration,
                      ChannelConnectionManager channelConnectionManager,
                      ClientConfiguration clientConfiguration) {
        bootstrap = new Bootstrap();
        this.nettyConfiguration = nettyConfiguration;
        this.clientConfiguration = clientConfiguration;
        this.channelConnectionManager = channelConnectionManager;
        handlerWorker = new DefaultEventExecutorGroup(nettyConfiguration.getHandlerWorks());
        if (PlatformUtil.isLinux()) {
            ioWorker = new EpollEventLoopGroup(nettyConfiguration.getIoWorkers());
            channelClass = EpollSocketChannel.class;
        } else {
            ioWorker = new NioEventLoopGroup(nettyConfiguration.getIoWorkers());
            channelClass = NioSocketChannel.class;
        }
        initBootStrap();
    }

    private void initBootStrap() {
        bootstrap.group(ioWorker).channel(channelClass)
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, nettyConfiguration.getIoWorkers())
                .option(ChannelOption.SO_RCVBUF, nettyConfiguration.getSo_receiveBuffer())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(handlerWorker, new FileEncoder())
                                .addLast(handlerWorker, new FileDecoder(nettyConfiguration.getMaxFrameLength(), 0, Integer.BYTES))
                                .addLast(handlerWorker, new SimpleChannelInboundHandler<FileCommand>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileCommand fileCommand) throws Exception {
                                        //上传报错，复位重传
                                        if (fileCommand.getProtocolType() == RequestCode.UPLOAD_RESET) {
                                            ResponseFuture.onRest(fileCommand.getRequestSeq());
                                        } else {
                                            ResponseFuture.onComplete(fileCommand.getRequestSeq(), fileCommand);
                                        }
                                    }
                                });
                    }
                });
    }

    public Channel getConnection(SocketAddress socketAddress) throws InterruptedException {
        Map<String, ChannelFuture> channelFutureTable = SocketChannelTableInfo.getChannelFutureTable();
        ChannelFuture channelFuture = channelFutureTable.get(socketAddress.toString());
        if (channelFuture != null && channelFuture.channel().isActive()) {
            return channelFuture.channel();
        }

        lock.tryLock(3000, TimeUnit.MILLISECONDS);
        boolean needNewConnect = true;
        ChannelFuture future = null;
        if (channelFuture != null) {
            //是否已连接
            if (channelFuture.channel().isActive()) {
                return channelFuture.channel();
                //还没执行连接尝试
            } else if (!channelFuture.isDone()) {
                future = channelFuture;
                needNewConnect = false;
                //尝试连接操作做了，且连接未处于激活，说明连接失败或者连接失效
            } else {
                channelFutureTable.remove(socketAddress.toString());
                needNewConnect = true;
            }
        }

        if (needNewConnect) {
            future = bootstrap.connect(socketAddress);
            channelFutureTable.put(socketAddress.toString(), future);
        }

        lock.unlock();

        if (future != null) {
            if (future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS)) {
                if (future.channel().isActive()) {
                    return future.channel();
                } else {
                    throw new RuntimeException("创建连接失败 addr: " + socketAddress.toString());
                }
            }
        }
        return null;
    }

    public FileCommand sendCommandSync(FileCommand request) throws InterruptedException {
        Channel channel = getConnection(new InetSocketAddress(clientConfiguration.getServerHostName(),
                clientConfiguration.getServerPort()));
        ResponseFuture responseFuture = new ResponseFuture();
        ResponseFuture.putToTable(request.getRequestSeq(), responseFuture);
        channel.writeAndFlush(request).sync();
        return responseFuture.get();
    }

}
