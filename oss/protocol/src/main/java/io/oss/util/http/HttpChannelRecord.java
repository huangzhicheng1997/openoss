package io.oss.util.http;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhicheng
 * @Date 2021/6/6 9:17 下午
 * @Version 1.0
 */
public class HttpChannelRecord {

    private static Map<String, Channel> httpChannelTable = new ConcurrentHashMap<>();

    public static void add(Channel channel) {
        httpChannelTable.put(channel.remoteAddress().toString(), channel);
    }

    public static boolean isHttpChannel(Channel channel) {
        return httpChannelTable.containsKey(channel.remoteAddress().toString());
    }

    public static void remove() {
        httpChannelTable.forEach((addr, channel) -> {
            if (!channel.isActive()) {
                httpChannelTable.remove(addr);
            }
        });
    }
}
