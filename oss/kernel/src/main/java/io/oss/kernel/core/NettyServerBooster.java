package io.oss.kernel.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.StringUtil;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.environment.KernelEnvironment;
import io.oss.kernel.exception.KernelException;
import io.oss.util.ChannelHandlerInitializer;
import io.oss.util.CodecHelp;
import io.oss.kernel.network.ConnectionMonitor;
import io.oss.kernel.spi.plugins.ChannelHandlerInitializerAware;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.support.ApplicationEventMultiCaster;
import io.oss.kernel.support.ApplicationLifeCycleManager;
import io.oss.kernel.support.processor.DispatcherProcessor;
import io.oss.kernel.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/4/12 7:52 下午
 * @Version 1.0
 */
public class NettyServerBooster {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ServerBootstrap serverBootstrap;
    private final CodecHelp codecHelp;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private EventExecutorGroup eventExecutors;
    private final IsolatedEnvironment isolatedEnvironment;
    private Class<? extends ServerChannel> serverChannel;
    private final ApplicationLifeCycleManager applicationLifeCycleManager;
    private final ApplicationEventMultiCaster applicationEventMultiCaster;
    private final Map<String, Component> componentMap;


    private Integer bossThreads = 3;
    private Integer workerThreads = 4;
    private Integer handlerThreads = 5;
    private Integer idleSeconds = 10;
    private Integer serverPort = 8999;

    public NettyServerBooster(CodecHelp codecHelp, IsolatedEnvironment isolatedEnvironment,
                              Map<String, Component> componentMap, Integer serverPort) {
        this.serverPort = serverPort;
        this.serverBootstrap = new ServerBootstrap();
        this.componentMap = componentMap;
        this.applicationEventMultiCaster = (ApplicationEventMultiCaster) componentMap.get("io.oss.kernel.support.ApplicationEventMultiCaster");
        this.applicationLifeCycleManager = (ApplicationLifeCycleManager) componentMap.get("io.oss.kernel.support.DefaultApplicationLifeCycleManager");
        this.codecHelp = codecHelp;
        this.isolatedEnvironment = isolatedEnvironment;
        serverConfigInit();
    }

    private void serverConfigInit() {
        try {
            String idleSecondsProperty = getKernelProperty(KernelEnvironment.IDLE_SECONDS_TIME);
            if (!StringUtil.isNullOrEmpty(idleSecondsProperty)) {
                this.idleSeconds = Integer.parseInt(idleSecondsProperty);
            }
            String bossThreadsProperty = getKernelProperty(KernelEnvironment.BOSS_THREADS);
            if (!StringUtil.isNullOrEmpty(bossThreadsProperty)) {
                this.bossThreads = Integer.parseInt(bossThreadsProperty);
            }
            String workerThreadsProperty = getKernelProperty(KernelEnvironment.WORKER_THREADS);
            if (!StringUtil.isNullOrEmpty(workerThreadsProperty)) {
                this.workerThreads = Integer.parseInt(workerThreadsProperty);
            }
            String handlerThreadsProperty = getKernelProperty(KernelEnvironment.HANDLER_THREADS);
            if (!StringUtil.isNullOrEmpty(handlerThreadsProperty)) {
                this.handlerThreads = Integer.parseInt(handlerThreadsProperty);
            }
        } catch (NumberFormatException e) {
            throw new KernelException("illegal attribute in 'kernel.properties'", e);
        }


    }


    public void fireAndStart() {

        choosePlatFormAndSpecialConfig();

        nettyOptionTakeEffect();

        nettyHandlersTakeEffect();

        serverStart();
    }


    private void serverStart() {
        serverBootstrap.bind(new InetSocketAddress(serverPort)).addListener(future -> {
            if (future.isSuccess() && logger.isDebugEnabled()) {
                logger.debug("server start port:" + serverPort);
                applicationLifeCycleManager.afterNettyServerStartSuccess(this);
            }
        });
    }

    private void nettyHandlersTakeEffect() {
        //初始化handler
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelHandlerInitializer channelHandlerInitializer = new ChannelHandlerInitializer(ch.pipeline());
                //初始化编解码器
                codecHelp.codecComponentInject(channelHandlerInitializer);
                codecHelp.setCodecEventExecutor(eventExecutors);
                //连接生命周期管理器
                channelHandlerInitializer.addLast(eventExecutors, new ConnectionMonitor(applicationEventMultiCaster));
                channelHandlerInitializer.addLast(eventExecutors, new IdleStateHandler(0, 0, idleSeconds));
                //处理器分发器
                Object o = componentMap.get(DispatcherProcessor.class.getName());
                if (o instanceof DispatcherProcessor) {
                    channelHandlerInitializer.addLast(eventExecutors, (DispatcherProcessor) o);
                } else {
                    throw new KernelException("no dispatchProcessor find!");
                }
                //用户自定义 添加handler
                componentMap.forEach((name, component) -> {
                    if (component instanceof ChannelHandlerInitializerAware) {
                        ((ChannelHandlerInitializerAware) component).aware(channelHandlerInitializer, eventExecutors);
                    }
                });

            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                logger.error("channel handler init exception :" + cause);
            }
        });
    }

    /**
     * netty 的option进行配置
     */
    private void nettyOptionTakeEffect() {
        Map<ChannelOption, Object> workerOptions = new HashMap<>();
        Map<ChannelOption, Object> bossOptions = new HashMap<>();
        this.applicationLifeCycleManager.beforeNettyOptionEffect(workerOptions, bossOptions);
        //设置boss,worker
        serverBootstrap.group(boss, worker).channel(serverChannel);
        //配置tcp参数
        bossOptions.forEach(serverBootstrap::option);
        workerOptions.forEach(serverBootstrap::childOption);
        this.applicationLifeCycleManager.afterNettyOptionEffect();

    }

    /**
     * 选择平台进行平台的特殊配置
     */
    private void choosePlatFormAndSpecialConfig() {
        if (PlatformUtil.isLinux()) {
            boss = new EpollEventLoopGroup(bossThreads);
            worker = new EpollEventLoopGroup(workerThreads);
            eventExecutors = new DefaultEventExecutorGroup(this.handlerThreads);
            serverChannel = EpollServerSocketChannel.class;
        } else {
            boss = new NioEventLoopGroup(bossThreads);
            worker = new NioEventLoopGroup(workerThreads);
            eventExecutors = new DefaultEventExecutorGroup(this.handlerThreads);
            serverChannel = NioServerSocketChannel.class;
        }
    }

    /**
     * 获取内核配置
     *
     * @param name 配置名
     * @return
     */
    private String getKernelProperty(String name) {
        return isolatedEnvironment.getPrivateProperty(KernelEnvironment.KernelEnvironmentName, name);
    }
}
