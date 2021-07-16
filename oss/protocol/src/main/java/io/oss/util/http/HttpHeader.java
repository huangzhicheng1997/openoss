package io.oss.util.http;

import io.oss.util.Header;

/**
 * @Author zhicheng
 * @Date 2021/6/4 2:13 下午
 * @Version 1.0
 */
public class HttpHeader implements Header {

    private String uri;

    private String accessToken;

    private static final String HEADER_NAME_ACCESS_TOKEN = "accessToken";


    @Override
    public String uri() {
        return uri;
    }

    @Override
    public Integer seq() {
        return null;
    }

    @Override
    public String accessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void setSeq(Integer seq) {

    }

}
