package io.oss.protocol.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import java.util.List;

/**
 * http报文转成，业务的"Command"
 *
 * @Author zhicheng
 * @Date 2021/6/3 6:24 下午
 * @Version 1.0
 */
public class HttpMsg2CommandDecoder extends MessageToMessageDecoder<FullHttpRequest> {

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        HttpRequestCommand httpRequestCommand = new HttpRequestCommand(msg);
        out.add(httpRequestCommand);
        HttpChannelRecord.add(ctx.channel());
    }
}