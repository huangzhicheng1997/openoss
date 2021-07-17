package io.oss.server.http.impl;

import io.oss.kernel.core.NettyServerBooster;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.event.NettyServerStartEvent;
import io.oss.kernel.spi.listener.ApplicationListener;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.protocol.http.HttpCodecHelp;
import io.oss.server.handler.DownloadHandler;
import io.oss.server.handler.UploadHandler;

import java.util.Collections;
import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/6/3 5:41 下午
 * @Version 1.0
 */
public class HttpServerImpl implements FindDependenciesComponent, EnvironmentAware, ApplicationListener<NettyServerStartEvent> {
    private Map<String, Component> componentMap;
    private IsolatedEnvironment environment;

    @Override
    public void setComponentDependencies(Map<String, Component> componentMap) {
        componentMap.remove(DownloadHandler.class.getName());
        componentMap.remove(UploadHandler.class.getName());
        this.componentMap = Collections.unmodifiableMap(componentMap);
    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void listen(NettyServerStartEvent applicationEvent) {
        NettyServerBooster nettyServerBooster = new NettyServerBooster(new HttpCodecHelp(), environment, componentMap, 8080);
        nettyServerBooster.fireAndStart();
    }

    @Override
    public double getOrder() {
        return Integer.MAX_VALUE - 100;
    }
}
