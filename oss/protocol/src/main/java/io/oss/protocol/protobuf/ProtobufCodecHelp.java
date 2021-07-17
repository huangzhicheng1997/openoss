package io.oss.protocol.protobuf;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.EventExecutorGroup;
import io.oss.protocol.ChannelHandlerInitializer;
import io.oss.protocol.CodecHelp;
import io.oss.protocol.protobuf.pbfile.ProtoBufProtocol;

/**
 * @Author zhicheng
 * @Date 2021/4/12 7:56 下午
 * @Version 1.0
 */
public class ProtobufCodecHelp implements CodecHelp {

    private EventExecutorGroup eventExecutorGroup;


    @Override
    public void codecComponentInject(ChannelHandlerInitializer channelHandlerInitializer) {
        if (null == eventExecutorGroup) {
            channelHandlerInitializer.addLast(new ProtobufVarint32FrameDecoder());
            channelHandlerInitializer.addLast(new ProtobufDecoder(ProtoBufProtocol.FileCommand.getDefaultInstance()));
            channelHandlerInitializer.addLast(new ProtobufWrappedDecoder());
            channelHandlerInitializer.addLast(new ProtobufVarint32LengthFieldPrepender());
            channelHandlerInitializer.addLast(new ProtobufEncoder());
            channelHandlerInitializer.addLast(new ProtobufUnWrapEncoder());
        } else {
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufVarint32FrameDecoder());
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufDecoder(ProtoBufProtocol.FileCommand.getDefaultInstance()));
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufWrappedDecoder());
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufVarint32LengthFieldPrepender());
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufEncoder());
            channelHandlerInitializer.addLast(eventExecutorGroup, new ProtobufUnWrapEncoder());
        }
    }

    @Override
    public void setCodecEventExecutor(EventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
    }
}
