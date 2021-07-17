package io.oss.protocol;

/**
 * @Author zhicheng
 * @Date 2021/4/10 2:30 下午
 * @Version 1.0
 */
public interface Header {

    /**
     * 获取请求路径，服务器根据此路径进行请求处理
     *
     * @return 路径
     */
    String uri();

    /**
     * 获取请求序号，序号为某个请求有效时间内的唯一标识
     *
     * @return 序号
     */
    Integer seq();

    String accessToken();

    void setAccessToken(String accessToken);


    void setUri(String uri);

    void setSeq(Integer seq);



}
