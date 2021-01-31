package io.oss.framework.remoting.codec;

import io.oss.framework.remoting.protocol.FileCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-01-22 15:12
 */
public class FileEncoder extends MessageToByteEncoder<FileCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FileCommand msg, ByteBuf out) throws Exception {
        ByteBuffer buffer = msg.encode();
        out.writeBytes(buffer);
    }
}
