package io.oss.server.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;
import io.oss.util.ChannelHandlerInitializer;
import io.oss.kernel.spi.plugins.ChannelHandlerInitializerAware;
import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.util.http.HttpChannelRecord;
import io.oss.util.util.KVPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一分钟内超过3次无心跳，则断开连接
 *
 * @Author zhicheng
 * @Date 2021/5/26 11:33 上午
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class HeartBeatCheckHandler extends ChannelDuplexHandler implements ChannelHandlerInitializerAware,
        WheelTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Channel, KVPair<AtomicInteger/*计数*/, Long/*第一次计数时间*/>> counter = new ConcurrentHashMap<>();

    @Override
    public void aware(ChannelHandlerInitializer channelHandlerInitializer, EventExecutorGroup eventExecutorGroup) {
        channelHandlerInitializer.addLast(eventExecutorGroup, this);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && !HttpChannelRecord.isHttpChannel(ctx.channel())) {
            KVPair<AtomicInteger, Long> countInfo = counter.get(ctx.channel());
            if (null == countInfo) {
                counter.put(ctx.channel(), new KVPair<>(new AtomicInteger(1), System.currentTimeMillis()));
            } else {
                int times = countInfo.getKey().incrementAndGet();
                if (times >= 3) {
                    ctx.channel().close().addListener(future -> {
                        if (future.isSuccess()) {
                            counter.remove(ctx.channel());
                            logger.debug("connection:" + ctx.channel().remoteAddress().toString() +
                                    " heartbeats have been checked " + times +
                                    " times but no heartbeat ,this connection has been disconnected!");
                        }
                    });
                }
            }
        }
    }


    @Override
    public void execute() {
        //恢复心跳的channel，不需要计数，需要进行清理
        counter.forEach((channel, kvPair) -> {
            //超过60秒还未清除，则为恢复心跳的连接
            if (System.currentTimeMillis() - kvPair.getV() >= 60000) {
                counter.remove(channel);
                logger.debug("useless heartbeat counter");
            }
        });
    }

    @Override
    public Long delayMillSeconds() {
        return 10000L;
    }

}
