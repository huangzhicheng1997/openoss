package io.oss.server.excption;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.oss.kernel.support.ChannelOuterExceptionHandler;
import io.oss.util.RemotingCall;
import io.oss.util.exception.FileNotFindException;
import io.oss.util.exception.NotFindProcessorException;
import io.oss.util.http.HttpChannelRecord;
import io.oss.util.http.HttpResponseCommand;

/**
 * @Author zhicheng
 * @Date 2021/6/14 3:19 下午
 * @Version 1.0
 */
public class FileNotFindExceptionHandler implements ChannelOuterExceptionHandler {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof FileNotFindException || ex.getCause() instanceof FileNotFindException
                ||ex instanceof NotFindProcessorException;
    }

    @Override
    public boolean invoke(ChannelHandlerContext context, Throwable ex) {
        if (HttpChannelRecord.isHttpChannel(context.channel())) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            String notFind = "<html>\n" +
                    "<head><title>404 Not Found</title></head>\n" +
                    "<body bgcolor=\"white\">\n" +
                    "<center><h1>404 Not Found</h1></center>\n" +
                    "<hr><center>netDisk</center>\n" +
                    "</body>\n" +
                    "</html>";
            response.content().writeBytes(notFind.getBytes());
            RemotingCall.writeAndFlush(context.channel(), new HttpResponseCommand(response));
            return false;
        } else {
            return true;
        }

    }
}
