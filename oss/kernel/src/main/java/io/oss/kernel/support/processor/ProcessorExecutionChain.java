package io.oss.kernel.support.processor;

import io.oss.protocol.Command;

import java.util.List;

/**
 * @author zhicheng
 * @date 2021-04-29 16:43
 */
public class ProcessorExecutionChain {

    private HandlerChainContext handlerChainContext;

    private ProcessorInterceptor head;

    private ProcessorInterceptor tail;

    private NettyProcessor nettyProcessor;

    private Command response;

    /**
     * 构建处理器执行链
     *
     * @param processorInterceptors 拦截器列表
     * @param processor             处理器
     */
    public void buildExecutionChain(List<ProcessorInterceptor> processorInterceptors,
                                    NettyProcessor processor,
                                    HandlerChainContext chainContext) {
        ProcessorInterceptor temp=null;
        for (int i = 0; i < processorInterceptors.size(); i++) {
            ProcessorInterceptor next = processorInterceptors.get(i);
            if (i == 0) {
                head = next;
                temp = head;
            }

            if (i == (processorInterceptors.size() - 1)) {
                tail = next;
            }
            temp.setNext(next);
            temp = temp.getNext();
        }
        this.nettyProcessor = processor;
        this.handlerChainContext = chainContext;
    }

    /**
     * 执行处理链
     *
     * @param request 请求
     */
    public void execute(Command request) {
        ProcessorInterceptor temp = head;
        execute0(temp, request);
    }


    private void execute0(ProcessorInterceptor node, Command request) {
        if (node == null) {
            response = nettyProcessor.handle(request, handlerChainContext);
            return;
        }
        if (!node.isMatch(request.getHeader().uri())) {
            if (node.hasNext()) {
                execute0(node.getNext(), request);
            }
            if (node == tail) {
                response = nettyProcessor.handle(request, handlerChainContext);
            }
        } else {
            if (node.preHandle(request, handlerChainContext)) {
                if (node.hasNext()) {
                    execute0(node.getNext(), request);
                }
                //node为tail，开始执行处理器
                if (node == tail) {
                    this.response = nettyProcessor.handle(request, handlerChainContext);
                }
            }

            /*
             * 断路情况下必然 不会走到处理器。
             */
            this.response = node.afterHandle(response, handlerChainContext);
        }
    }

    public Command getResponse() {
        return this.response;
    }
}
