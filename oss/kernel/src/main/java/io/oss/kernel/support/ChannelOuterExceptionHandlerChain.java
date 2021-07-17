package io.oss.kernel.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.oss.protocol.ChannelHandlerInitializer;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.environment.KernelEnvironment;
import io.oss.kernel.spi.plugins.ChannelHandlerInitializerAware;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhicheng
 * @date 2021-05-08 15:24
 */
public class ChannelOuterExceptionHandlerChain implements ChannelHandlerInitializerAware, FindDependenciesComponent, EnvironmentAware, ComponentInitializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DefaultEventExecutorGroup executorGroup;

    private final List<ChannelOuterExceptionHandler> channelOuterExceptionHandlers = new ArrayList<>();

    private Integer exceptionHandleThreads;

    @Override
    public void aware(ChannelHandlerInitializer channelHandlerInitializer, EventExecutorGroup eventExecutorGroup) {
        channelHandlerInitializer.addLast(executorGroup, new ExceptionHandler());
    }

    @Override
    public void setComponentDependencies(Map<String, Component> componentMap) {

        componentMap.values().forEach(component -> {
            if (component instanceof ChannelOuterExceptionHandler) {
                channelOuterExceptionHandlers.add((ChannelOuterExceptionHandler) component);
            }
        });

        channelOuterExceptionHandlers.sort(Comparator.comparingInt(ChannelOuterExceptionHandler::order));

        if (logger.isDebugEnabled()) {
            StringBuilder debugMsg = new StringBuilder();
            debugMsg.append("exceptionHandlers:");
            for (ChannelOuterExceptionHandler channelOuterExceptionHandler : channelOuterExceptionHandlers) {
                debugMsg.append("->").append(channelOuterExceptionHandler.getName());
            }
            logger.debug(debugMsg.toString());
        }

    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        String exceptionHandleThreads = environment.getPrivateProperty(KernelEnvironment.KernelEnvironmentName, "kernel.exception.handle.threads");
        this.exceptionHandleThreads = Integer.valueOf(exceptionHandleThreads);
    }

    @Override
    public void afterInit() {
        this.executorGroup = new DefaultEventExecutorGroup(exceptionHandleThreads);
    }

    class ExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            handle(ctx, cause);
            super.exceptionCaught(ctx, cause);
        }
    }


    public void handle(ChannelHandlerContext ctx, Throwable cause) {
        AtomicReference<Boolean> isContinue = new AtomicReference<>(true);
        channelOuterExceptionHandlers.forEach(channelOuterExceptionHandler -> {
            if (channelOuterExceptionHandler.support(cause) && isContinue.get()) {
                isContinue.set(channelOuterExceptionHandler.invoke(ctx, cause));
            }
        });
    }
}
