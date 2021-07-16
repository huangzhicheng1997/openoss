package io.oss.util.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.oss.util.protobuf.pbfile.ProtoBufProtocol;

import java.util.List;

/**
 * 发送报文时将报文解包为原始Protobuf报文
 *
 * @author zhicheng
 * @date 2021-05-07 16:04
 */
public class ProtobufUnWrapEncoder extends MessageToMessageEncoder<PBCommandAdaptor> {
    @Override
    protected void encode(ChannelHandlerContext ctx, PBCommandAdaptor msg, List<Object> out) throws Exception {
        Object o = msg.unWrap();
        if (o instanceof ProtoBufProtocol.FileCommand) {
            out.add(o);
        }
    }
}
