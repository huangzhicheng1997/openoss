package io.oss.util.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.oss.util.protobuf.pbfile.ProtoBufProtocol;

import java.util.List;

/**
 * 封装protobuf报文，转化为系统可兼容的报文
 *
 * @author zhicheng
 * @date 2021-05-05 15:44
 */
public class ProtobufWrappedDecoder extends MessageToMessageDecoder<ProtoBufProtocol.FileCommand> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ProtoBufProtocol.FileCommand msg, List<Object> out) throws Exception {
        PBCommandAdaptor pbCommandAdaptor = new PBCommandAdaptor(msg);
        out.add(pbCommandAdaptor);
    }
}
