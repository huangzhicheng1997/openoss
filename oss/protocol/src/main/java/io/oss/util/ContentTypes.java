package io.oss.util;

import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * @Author zhicheng
 * @Date 2021/6/8 2:56 下午
 * @Version 1.0
 */
public class ContentTypes {
    public static final String APPLICATION_JSON = HttpHeaderValues.APPLICATION_JSON.toLowerCase().toString();
    public static final String APPLICATION_OCTET_STREAM = HttpHeaderValues.APPLICATION_OCTET_STREAM.toLowerCase().toString();
    public static final String IMAGE = "image/";
}
