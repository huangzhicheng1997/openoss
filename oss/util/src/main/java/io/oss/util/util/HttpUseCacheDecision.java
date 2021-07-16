package io.oss.util.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.internal.StringUtil;

/**
 * @Author zhicheng
 * @Date 2021/6/13 4:26 下午
 * @Version 1.0
 */
public class HttpUseCacheDecision {

    public static boolean isUseCacheByEtag(FullHttpRequest request, String responseEtag) {
        String ifNoneMatch = request.headers().get(HttpHeaderNames.IF_NONE_MATCH);
        return !StringUtil.isNullOrEmpty(ifNoneMatch) && ifNoneMatch.equals(responseEtag);
    }

    public static boolean isUseCacheByLastMod(FullHttpRequest request, String responseMod) {
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        return !StringUtil.isNullOrEmpty(ifModifiedSince) && ifModifiedSince.equals(responseMod);
    }

    public static boolean isUseCache(String responseEtag, String responseMod, FullHttpRequest request) {
        return isUseCacheByEtag(request, responseEtag) && isUseCacheByLastMod(request, responseMod);
    }
}
