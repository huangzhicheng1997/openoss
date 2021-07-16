package io.oss.kernel.spi.plugins;

import io.netty.channel.ChannelOption;

import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/4/25 7:36 下午
 * @Version 1.0
 */
public interface NettyOptionConfigAware extends Component {

    public void aware(Map<ChannelOption, Object> workerOptions, Map<ChannelOption, Object> bossOptions);

}
