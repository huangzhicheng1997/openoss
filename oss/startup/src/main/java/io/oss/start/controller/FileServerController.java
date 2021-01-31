package io.oss.start.controller;


import io.oss.file.upload.FileUploadProcessor;
import io.oss.framework.config.NettyConfiguration;
import io.oss.framework.config.ServerConfiguration;
import io.oss.framework.core.InitializationContext;
import io.oss.framework.remoting.FileServer;
import io.oss.framework.remoting.handler.ChannelConnectionManager;
import io.oss.framework.remoting.handler.DispatcherChannelHandler;
import io.oss.framework.remoting.listener.ChannelEventMultiCaster;
import io.oss.framework.remoting.listener.HeartBeatChecker;
import io.oss.framework.remoting.listener.SocketChannelTableInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhicheng
 * @date 2021-01-22 17:04
 */
public class FileServerController {


    public static void main(String[] args) {


        NettyConfiguration nettyConfiguration = initConfiguration();
        ServerConfiguration serverConfiguration = initServerConfiguration();
        //
        initContext(nettyConfiguration, serverConfiguration);

        DispatcherChannelHandler dispatcherChannelHandler = initDispatcherChannelHandler();
        ChannelConnectionManager channelConnectionManager = initChannelConnectManager();

        FileServer fileServer = new FileServer(nettyConfiguration, dispatcherChannelHandler, channelConnectionManager, serverConfiguration);
        fileServer.start();

    }

    private static void initContext(NettyConfiguration nettyConfiguration, ServerConfiguration serverConfiguration) {
        //文件上传处理器
        FileUploadProcessor fileUploadProcessor = new FileUploadProcessor(serverConfiguration);
        //心跳检测
        HeartBeatChecker heartBeatChecker = new HeartBeatChecker();
        //socketChannel记录表
        SocketChannelTableInfo socketChannelTableInfo = new SocketChannelTableInfo();
        //注册
        context().registerHandler(fileUploadProcessor);
        context().registerEventListener(fileUploadProcessor, heartBeatChecker, socketChannelTableInfo);
    }

    private static ServerConfiguration initServerConfiguration() {
        return new ServerConfiguration();
    }

    private static NettyConfiguration initConfiguration() {
        return new NettyConfiguration();
    }

    /**
     * 业务逻辑处理
     *
     * @return
     */
    private static DispatcherChannelHandler initDispatcherChannelHandler() {
        //处理器分发器
        DispatcherChannelHandler dispatcherChannelHandler = new DispatcherChannelHandler();
        dispatcherChannelHandler.addLast(context().getHandler("fileUploadProcessor"));
        return dispatcherChannelHandler;
    }

    private static InitializationContext context() {
        return InitializationContext.getInstance();
    }

    /**
     * 连接相关的处理
     *
     * @return
     */
    private static ChannelConnectionManager initChannelConnectManager() {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        ChannelEventMultiCaster channelEventMultiCaster = new ChannelEventMultiCaster(executorService);
        //心跳检测
        channelEventMultiCaster.register(context().getListener("heartBeatChecker"));
        //channel表
        channelEventMultiCaster.register(context().getListener("socketChannelTableInfo"));

        channelEventMultiCaster.register(context().getListener("fileUploadProcessor"));
        return new ChannelConnectionManager(channelEventMultiCaster);
    }
}
