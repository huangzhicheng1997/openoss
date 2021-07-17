package io.oss.protocol;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.oss.protocol.http.HttpChannelRecord;

/**
 * @Author zhicheng
 * @Date 2021/6/6 9:51 下午
 * @Version 1.0
 */
public class RemotingCall {

    public static void writeAndFlush(Channel channel, Command command) {
        if (channel.isActive()) {
            channel.writeAndFlush(command).addListener(future -> {
                if (HttpChannelRecord.isHttpChannel(channel) && future.isSuccess()) {
                    channel.close();
                }
            });
        }
    }

    public static void writeAndFlush(Channel channel, Command command, GenericFutureListener<? extends Future<? super Void>> futureListener) {
        if (channel.isActive()) {
            channel.writeAndFlush(command).addListener(future -> {
                if (HttpChannelRecord.isHttpChannel(channel) && future.isSuccess()) {
                    channel.close();
                }
            }).addListener(futureListener);
        }
    }
}
