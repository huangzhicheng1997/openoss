package io.oss.framework.remoting.listener;

import io.oss.framework.remoting.listener.event.ChannelActiveEvent;
import io.oss.framework.remoting.protocol.Component;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhicheng
 * @date 2021-01-22 16:15
 */
@Component(name ="socketChannelTableInfo" )
public class SocketChannelTableInfo extends AbstractChannelEventListener {
    /**
     * 用做服务端时有值，值为'连接socket'
     */
    private static Map<String/*addr*/, Channel> channelTable = new ConcurrentHashMap<>();

    /**
     * 作为客户端时有值
     */
    private static Map<String/*addr*/, ChannelFuture> channelFutureTable = new ConcurrentHashMap<>();

    @Override
    public void onActive(ChannelActiveEvent evt) {
        Channel channel = evt.getCtx().channel();
        channelTable.put(channel.remoteAddress().toString(), channel);
    }

    public static Channel getChannelByAddr(String addr) {
        return channelTable.get(addr);
    }

    public static void removeChannelFromTable(Channel channel) {
        channelTable.remove(channel.remoteAddress().toString());
    }

    public static Map<String, ChannelFuture> getChannelFutureTable() {
        return channelFutureTable;
    }


}
