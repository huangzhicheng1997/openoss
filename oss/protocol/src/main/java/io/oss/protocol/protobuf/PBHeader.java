package io.oss.protocol.protobuf;


import io.oss.protocol.Header;

/**
 * @Author zhicheng
 * @Date 2021/4/10 3:23 下午
 * @Version 1.0
 */
public class PBHeader implements Header {

    private String uri;

    private Integer seq;

    private Integer headerLength;

    private Integer bodyLength;

    private String accessToken;

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public Integer seq() {
        return seq;
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
        this.seq = seq;
    }
}
