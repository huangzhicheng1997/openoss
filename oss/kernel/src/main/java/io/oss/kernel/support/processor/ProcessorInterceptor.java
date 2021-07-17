package io.oss.kernel.support.processor;

import io.oss.kernel.spi.plugins.Component;
import io.oss.protocol.Command;

/**
 * @author zhicheng
 * @date 2021-04-29 15:27
 */
public interface ProcessorInterceptor extends Component {

    /**
     * 请求路径是否匹配
     *
     * @param uri 请求路径
     * @return 是否匹配
     */
    boolean isMatch(String uri);

    /**
     * 前置处理
     *
     * @param request 请求协议
     * @param context 上下文
     * @return 是否进行执行
     */
    boolean preHandle(Command request, HandlerChainContext context);

    /**
     * 后置处理
     *
     * @param response 返回值
     * @param context  上下文
     */
    Command afterHandle(Command response, HandlerChainContext context);

    /**
     * 获取下一个节点
     *
     * @return node
     */
    ProcessorInterceptor getNext();

    /**
     * 设置下一个节点
     *
     * @param processorInterceptor node
     */
    void setNext(ProcessorInterceptor processorInterceptor);

    /**
     * 是否存在下一个节点
     *
     * @return 是否存在
     */
    boolean hasNext();

    default Integer getOrder() {
        return 0;
    }
}
