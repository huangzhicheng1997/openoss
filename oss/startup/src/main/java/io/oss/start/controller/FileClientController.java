package io.oss.start.controller;

import io.oss.framework.config.ClientConfiguration;
import io.oss.framework.config.NettyConfiguration;
import io.oss.file.service.FileUploadHelper;
import io.oss.framework.remoting.FileClient;
import io.oss.framework.remoting.handler.ChannelConnectionManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FileClientController {

    public static void main(String[] args) throws InterruptedException, IOException {
        ChannelConnectionManager channelConnectionManager = initChannelConnectManager();
        NettyConfiguration nettyConfiguration = initConfiguration();
        ClientConfiguration clientConfiguration = initClientConfiguration();
        FileClient fileClient = new FileClient(nettyConfiguration, channelConnectionManager, clientConfiguration);
        String path = "/Users/huangzhicheng/Desktop/证件照.jpeg";
        FileUploadHelper fileUploadHelper = new FileUploadHelper(fileClient, path, 1<<19,
                new InetSocketAddress(clientConfiguration.getServerHostName(), clientConfiguration.getServerPort()));
        fileUploadHelper.startUpload();
    }

    private static ClientConfiguration initClientConfiguration() {
        return new ClientConfiguration();
    }

    private static NettyConfiguration initConfiguration() {
        return new NettyConfiguration();
    }

    private static ChannelConnectionManager initChannelConnectManager() {
        return new ChannelConnectionManager();
    }
}
