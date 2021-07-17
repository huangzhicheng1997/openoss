package io.oss.protocol.http;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.oss.protocol.Body;
import io.oss.protocol.Command;
import io.oss.protocol.Header;

/**
 * @Author zhicheng
 * @Date 2021/6/13 3:13 下午
 * @Version 1.0
 */
public class HttpResponseCommand implements Command {
    private DefaultFullHttpResponse response;

    public HttpResponseCommand(DefaultFullHttpResponse response) {
        this.response = response;
    }

    @Override
    public Header getHeader() {
        return new HttpHeader();
    }

    @Override
    public Body getBody() {
        HttpBody body = new HttpBody();
        body.putBuffer(response.content().nioBuffer());
        return body;
    }

    @Override
    public Object unWrap() {
        return response;
    }
}
