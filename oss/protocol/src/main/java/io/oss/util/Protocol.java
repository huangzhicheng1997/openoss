package io.oss.util;

/**
 * @Author zhicheng
 * @Date 2021/4/10 2:27 下午
 * @Version 1.0
 */
public interface Protocol {

    public void setHeader(Header header);

    public void setBody(Body body);

    public Header getHeader();

    public Body getBody();

}
