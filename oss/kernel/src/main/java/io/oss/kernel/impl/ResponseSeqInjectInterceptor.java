package io.oss.kernel.impl;

import io.oss.kernel.support.processor.AbstractProcessorInterceptor;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.protocol.Command;
import io.oss.protocol.CommandBuilder;
import io.oss.protocol.http.HttpChannelRecord;

import java.util.Set;

/**
 * 非http请求，对响应注入对应请求的序号，以完成一次会话
 *
 * @author zhicheng
 * @date 2021-05-07 17:17
 */
public class ResponseSeqInjectInterceptor extends AbstractProcessorInterceptor {

    private final ThreadLocal<Integer> remoteRequestSeq = new ThreadLocal<>();

    @Override
    public boolean preHandle(Command request, HandlerChainContext context) {
        if (!HttpChannelRecord.isHttpChannel(getChannel(context))) {
            Integer seq = request.getHeader().seq();
            remoteRequestSeq.set(seq);
        }
        return true;
    }

    @Override
    public Command afterHandle(Command response, HandlerChainContext context) {
        if (null != response && !HttpChannelRecord.isHttpChannel(getChannel(context))) {
            response = CommandBuilder.replaceSeq(getChannel(context), response, remoteRequestSeq.get());
            remoteRequestSeq.remove();
        }
        return response;
    }

    @Override
    protected void matchURIInit(Set<String> matchUri) {
        matchUri.add("/**");
    }

    @Override
    public Integer getOrder() {
        return -1;
    }
}
