package io.oss.kernel.spi.event;

import io.oss.kernel.core.NettyServerBooster;

/**
 * @author zhicheng
 * @date 2021-05-07 17:00
 */
public class NettyServerStartEvent implements ApplicationEvent {
    private NettyServerBooster booster;

    public NettyServerStartEvent(NettyServerBooster booster) {
        this.booster = booster;
    }

    @Override
    public Object getSource() {
        return booster;
    }
}
