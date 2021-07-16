package io.oss.util.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.oss.util.Command;

import java.util.List;

/**
 * @Author zhicheng
 * @Date 2021/6/6 6:57 下午
 * @Version 1.0
 */
public class Command2HttpMsgEncoder extends MessageToMessageEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, List<Object> out) throws Exception {
        out.add(msg.unWrap());
    }


}
