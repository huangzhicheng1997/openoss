package io.oss.util;

import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/4/10 2:31 下午
 * @Version 1.0
 */
public interface Body {
    /**
     * 流式数据
     *
     * @return buffer
     */
    ByteBuffer buffer();

    /**
     * 返回
     *
     * @return string
     */
    String resp();

    /**
     * 流式数据
     *
     * @return buffer
     */
    void putBuffer(ByteBuffer byteBuffer);

    /**
     * 返回
     *
     * @return string
     */
    void setResp(String resp);
}
