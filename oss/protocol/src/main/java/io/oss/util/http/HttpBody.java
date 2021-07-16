package io.oss.util.http;

import io.oss.util.Body;

import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/6/4 2:17 下午
 * @Version 1.0
 */
public class HttpBody implements Body {


    private ByteBuffer buf;

    private String resp;

    public HttpBody() {
    }

    @Override
    public ByteBuffer buffer() {
        return buf;
    }

    @Override
    public String resp() {
        return resp;
    }

    @Override
    public void putBuffer(ByteBuffer byteBuffer) {
        this.buf = byteBuffer;
    }

    @Override
    public void setResp(String resp) {
        this.resp = resp;
    }
}
