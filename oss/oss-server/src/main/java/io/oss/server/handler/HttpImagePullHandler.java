package io.oss.server.handler;

import io.netty.handler.codec.http.*;
import io.oss.file.service.PullFileServiceWrapper;
import io.oss.kernel.Inject;
import io.oss.kernel.support.AutoDependenciesInjector;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.kernel.support.processor.NettyProcessor;
import io.oss.protocol.Command;
import io.oss.protocol.CommandBuilder;
import io.oss.protocol.ContentTypes;
import io.oss.util.exception.FileNotFindException;
import io.oss.util.util.FileUtil;
import io.oss.util.util.HttpUseCacheDecision;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @Author zhicheng
 * @Date 2021/6/8 6:25 下午
 * @Version 1.0
 */
public class HttpImagePullHandler implements AutoDependenciesInjector, NettyProcessor {

    @Inject
    private PullFileServiceWrapper pullFileService;

    private static final String IMAGE_PREFIX = "/oss/image";

    @Override
    public Command handle(Command request, HandlerChainContext context) {

        String uri;
        String filePath;

        try {
            uri = request.getHeader().uri();
            filePath = uri.substring(IMAGE_PREFIX.length());
        } catch (Exception e) {
            throw new FileNotFindException();
        }

        //文件不存在
        if (!(FileChecker.fileIsExist(filePath) && FileChecker.isImage(filePath))) {
            throw new FileNotFindException();
        }

        long fileLength = pullFileService.getFileLength(filePath);

        Date lastModifiedTime = new Date(new File(filePath).lastModified());
        String etag = countEtag(lastModifiedTime, fileLength);

        //检验cache
        if (HttpUseCacheDecision.isUseCache(etag, lastModifiedTime.toString(), (FullHttpRequest) request.unWrap())) {
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
            return CommandBuilder.fullHttpResponseCommand(defaultFullHttpResponse);
        }


        ByteBuffer buffer = pullFileService.pullPartOfFile(filePath, 0L, (int) fileLength);

        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        defaultFullHttpResponse.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES)
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.MAX_AGE + "=" + "1800")
                .set(HttpHeaderNames.CONTENT_LENGTH, fileLength)
                .set(HttpHeaderNames.CONTENT_TYPE, ContentTypes.IMAGE + FileUtil.getFileSuffix(filePath))
                .set(HttpHeaderNames.DATE, new Date().toString())
                .set(HttpHeaderNames.LAST_MODIFIED, lastModifiedTime.toString())
                .set(HttpHeaderNames.ETAG, etag)
                .set(HttpHeaderNames.SERVER, "Netty");
        defaultFullHttpResponse.content().writeBytes(buffer);
        return CommandBuilder.fullHttpResponseCommand(defaultFullHttpResponse);
    }

    private String countEtag(Date lastModifyTime, long fileLength) {
        long sec = lastModifyTime.getTime() / 1000;
        return Long.toHexString(sec) + Long.toHexString(fileLength);
    }

    @Override
    public List<String> getMappingUri() {
        return Collections.singletonList("/oss/image/**");
    }

}
