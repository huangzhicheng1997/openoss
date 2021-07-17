package io.oss.kernel.support.processor;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.oss.kernel.Inject;
import io.oss.kernel.environment.KernelEnvironment;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.support.*;
import io.oss.protocol.Command;
import io.oss.util.ConfigurationEnum;
import io.oss.protocol.Header;
import io.oss.protocol.RemotingCall;
import io.oss.protocol.exception.NotFindProcessorException;
import io.oss.protocol.http.HttpChannelRecord;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhicheng
 * @date 2021-04-29 16:38
 */
@ChannelHandler.Sharable
public class DispatcherProcessor extends PropertiesAvailableChannelInBindHandler<Command> implements AutoDependenciesInjector
        , ComponentInitializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<ProcessorInterceptor> processorInterceptors;

    @Inject
    private InterceptorRegister interceptorRegister;

    @Inject
    private ChannelOuterExceptionHandlerChain channelOuterExceptionHandlerChain;

    @Inject
    private CompositeProcessorOwner compositeProcessorOwner;


    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private ThreadPoolExecutor commonHandlerThreadPool;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command request) {
        ProcessorExecutionChain processorExecutionChain = new ProcessorExecutionChain();
        HandlerChainContext handlerChainContext = new HandlerChainContext();
        handlerChainContext.addAttr(HandlerChainContext.CHANNEL_CONTEXT, ctx);

        NettyProcessor processor = findProcessor(request);
        processorExecutionChain.buildExecutionChain(processorInterceptors, processor, handlerChainContext);

        ThreadPoolExecutor executor = this.commonHandlerThreadPool;
        if (null != processor.handlerExecutor()) {
            executor = processor.handlerExecutor();
        }

        //执行
        Future<?> submit = executor.submit(() -> {
            try {
                processorExecutionChain.execute(request);
                Command response = processorExecutionChain.getResponse();
                if (null != response) {
                    RemotingCall.writeAndFlush(ctx.channel(), response);
                }
            } catch (Exception e) {
                channelOuterExceptionHandlerChain.handle(ctx, e);
            }
        });
        String properties = getProperties(ConfigurationEnum.PROCESSOR_TIMEOUT.getKey());

        if (HttpChannelRecord.isHttpChannel(ctx.channel())) {
            executor.execute(() -> {
                try {
                    submit.get(Integer.parseInt(properties), TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    ctx.channel().closeFuture();
                }

            });
        }

    }

    /**
     * 查找所有的handler
     *
     * @param msg
     * @return
     */
    private NettyProcessor findProcessor(Command msg) {
        Header header = msg.getHeader();
        NettyProcessor nettyProcessor = null;
        for (String uri : getProcessorMapping().keySet()) {
            if (antPathMatcher.match(uri, msg.getHeader().uri())) {
                nettyProcessor = getProcessorMapping().get(uri);
                break;
            }
        }
        if (null == nettyProcessor) {
            throw new NotFindProcessorException("not find processor uri:" + header.uri());
        }
        return nettyProcessor;
    }


    @Override
    public void afterInit() {
        interceptorLoad();
        dispatcherProcessorPoolLoad();
    }

    /**
     * 发现所有的处理器
     */
    private void dispatcherProcessorPoolLoad() {
        int poolSize = Integer.parseInt(getProperties(ConfigurationEnum.PROCESSOR_COMMON_THREAD_POOL_SIZE.getKey()));
        commonHandlerThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("processor-executor:" + counter.getAndIncrement());
                return thread;
            }
        });
    }

    /**
     * 加载所有的拦截器
     */
    private void interceptorLoad() {
        this.processorInterceptors = interceptorRegister.getProcessorInterceptors();
        if (logger.isDebugEnabled()) {
            StringBuilder debugMsg = new StringBuilder();
            debugMsg.append("interceptors:");
            for (int i = 0; i < processorInterceptors.size(); i++) {
                debugMsg.append(processorInterceptors.get(i).getName());
                if (i != processorInterceptors.size() - 1) {
                    debugMsg.append("->");
                }
            }
            logger.debug(debugMsg.toString());
        }
    }

    @Override
    protected String getEnvironmentName() {
        return KernelEnvironment.KernelEnvironmentName;
    }

    private Map<String, NettyProcessor> getProcessorMapping() {
        return this.compositeProcessorOwner.processorMapping();
    }

}
