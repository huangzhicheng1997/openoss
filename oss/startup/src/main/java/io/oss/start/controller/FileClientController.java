package io.oss.start.controller;

import io.oss.file.upload.UploadProgress;
import io.oss.file.upload.UploadResultFuture;
import io.oss.framework.config.ClientConfiguration;
import io.oss.framework.config.NettyConfiguration;
import io.oss.file.service.FileUploadHelper;
import io.oss.framework.remoting.FileClient;
import io.oss.framework.remoting.handler.ChannelConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class FileClientController {

    public static void main(String[] args) throws InterruptedException, IOException {
        ChannelConnectionManager channelConnectionManager = initChannelConnectManager();
        NettyConfiguration nettyConfiguration = initConfiguration();
        ClientConfiguration clientConfiguration = initClientConfiguration();
        FileClient fileClient = new FileClient(nettyConfiguration, channelConnectionManager, clientConfiguration);
        String path = "C:\\Users\\lszhichengh\\Desktop\\platform-server.war";



        FileUploadHelper fileUploadHelper = new FileUploadHelper(fileClient, path, 65535,
                new InetSocketAddress(clientConfiguration.getServerHostName(), clientConfiguration.getServerPort()),
                Executors.newCachedThreadPool());
        UploadResultFuture uploadResultFuture = fileUploadHelper.startUpload();

        uploadResultFuture.addListener(future -> {
            UploadProgress uploadProgress = future.getUploadProgress();
            System.out.println("上传进度:"+uploadProgress.getProgress());
            if (future.isOK()){
                System.out.println("上传完成用时:"+uploadProgress.getUseTime());
            }
        });


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
