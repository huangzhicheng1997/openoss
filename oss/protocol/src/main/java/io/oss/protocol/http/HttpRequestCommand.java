package io.oss.protocol.http;

import io.netty.handler.codec.http.*;
import io.oss.protocol.Body;
import io.oss.protocol.Command;
import io.oss.protocol.ContentTypes;
import io.oss.protocol.Header;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author zhicheng
 * @Date 2021/6/4 2:12 下午
 * @Version 1.0
 */
public class HttpRequestCommand implements Command {
    private static final String APPLICATION_JSON = ContentTypes.APPLICATION_JSON;
    private static final String APPLICATION_OCTET_STREAM = ContentTypes.APPLICATION_OCTET_STREAM;
    private static final String HEADER_NAME_TOKEN = "accessToken";

    private FullHttpRequest request;

    public HttpRequestCommand(FullHttpRequest request) {
        this.request = request;
    }

    @Override
    public Header getHeader() {
        HttpHeaders headers = request.headers();
        HttpHeader header = new HttpHeader();
        try {
            header.setUri(URLDecoder.decode(request.uri(), StandardCharsets.UTF_8.name()));
            header.setAccessToken(URLDecoder.decode(headers.get(HEADER_NAME_TOKEN, StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return header;
    }

    @Override
    public Body getBody() {
        HttpBody body = new HttpBody();
        body.putBuffer(request.content().nioBuffer());
        return body;
    }

    @Override
    public Object unWrap() {
        return request;
    }

    public QueryStringDecoder getParameters() {
        String uri = request.uri();
        return new QueryStringDecoder(uri);

    }
}
