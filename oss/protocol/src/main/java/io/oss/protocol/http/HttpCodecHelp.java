package io.oss.protocol.http;

import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.EventExecutorGroup;
import io.oss.protocol.ChannelHandlerInitializer;
import io.oss.protocol.CodecHelp;

/**
 * @Author zhicheng
 * @Date 2021/6/3 5:47 下午
 * @Version 1.0
 */
public class HttpCodecHelp implements CodecHelp {
    @Override
    public void codecComponentInject(ChannelHandlerInitializer channelHandlerInitializer) {
        channelHandlerInitializer.addLast(new HttpServerCodec());
        channelHandlerInitializer.addLast(new HttpObjectAggregator(65535));
        channelHandlerInitializer.addLast(new HttpMsg2CommandDecoder());
        channelHandlerInitializer.addLast(new Command2HttpMsgEncoder());

    }



    @Override
    public void setCodecEventExecutor(EventExecutorGroup eventExecutorGroup) {


    }
}
