package io.oss.kernel.support.processor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.oss.kernel.spi.plugins.ComponentInitializer;
import io.oss.kernel.support.AntPathMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * 提供对uri注册的基类拦截器部分实现
 *
 * @author zhicheng
 * @date 2021-04-29 17:03
 */
public abstract class AbstractProcessorInterceptor implements ProcessorInterceptor, ComponentInitializer {

    private final Set<String> matchUri = new HashSet<>();

    private final Set<String> excludeUri = new HashSet<>();

    private ProcessorInterceptor next;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean isMatch(String uri) {
        boolean isMatch = false;

        if (matchUri.stream().anyMatch(pattern -> antPathMatcher.match(pattern, uri))) {
            isMatch = true;
        }

        /*
         * 不拦截的路径 具有更高级的优先级。
         */
        if (excludeUri.stream().anyMatch(pattern -> antPathMatcher.match(pattern, uri))) {
            isMatch = false;
        }

        return isMatch;
    }

    @Override
    public void afterInit() {
        //初始化
        excludeUri(excludeUri);
        matchURIInit(matchUri);

    }

    /**
     * 可重写进行设置
     *
     * @param excludeUri 不包含的路径集合
     */
    protected void excludeUri(Set<String> excludeUri) {
    }


    @Override
    public ProcessorInterceptor getNext() {
        return this.next;
    }

    @Override
    public boolean hasNext() {
        return null != this.next;
    }

    @Override
    public void setNext(ProcessorInterceptor processorInterceptor) {
        this.next = processorInterceptor;
    }

    /**
     * 可重写进行配置
     *
     * @param matchUri 匹配的uri
     */
    protected abstract void matchURIInit(Set<String> matchUri);



    protected Channel getChannel(HandlerChainContext chainContext) {
        ChannelHandlerContext channelHandlerContext = (ChannelHandlerContext) chainContext.getAttr(HandlerChainContext.CHANNEL_CONTEXT);
        return channelHandlerContext.channel();
    }


}
