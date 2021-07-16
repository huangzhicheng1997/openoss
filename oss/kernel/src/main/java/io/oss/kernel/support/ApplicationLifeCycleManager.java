package io.oss.kernel.support;

import io.netty.channel.ChannelOption;
import io.oss.kernel.core.NettyServerBooster;
import io.oss.kernel.environment.IsolatedEnvironment;

import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/4/10 7:18 下午
 * @Version 1.0
 */
public interface ApplicationLifeCycleManager {

    /**
     * 系统环境准备完毕
     *
     * @param environment 环境
     */
    void afterEnvironmentPrepared(IsolatedEnvironment environment);

    /**
     * 准备运行阶段，组件已经加载完毕
     */
    void afterPreparePlugins();


    /**
     * netty参数配置前
     *
     * @param workerOptions
     * @param bossOptions
     */
    void beforeNettyOptionEffect(Map<ChannelOption, Object> workerOptions, Map<ChannelOption, Object> bossOptions);

    /**
     * netty参数生效后
     */
    void afterNettyOptionEffect();

    /**
     * netty服务器启动成功
     * @param nettyServerBooster
     */
    void afterNettyServerStartSuccess(NettyServerBooster nettyServerBooster);
}
