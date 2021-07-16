package io.oss.util.protobuf;


import io.oss.util.Body;

import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/4/10 3:41 下午
 * @Version 1.0
 */
public class PBBody implements Body {
    private ByteBuffer buffer;
    private String resp;


    @Override
    public ByteBuffer buffer() {
        return buffer;
    }

    @Override
    public String resp() {
        return resp;
    }

    @Override
    public void putBuffer(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
    }

    @Override
    public void setResp(String resp) {
        this.resp = resp;
    }
}
