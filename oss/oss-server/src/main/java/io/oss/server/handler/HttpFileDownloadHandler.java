package io.oss.server.handler;

import io.netty.handler.codec.http.*;
import io.oss.file.service.PullFileServiceWrapper;
import io.oss.kernel.Inject;
import io.oss.kernel.support.AutoDependenciesInjector;
import io.oss.util.exception.PullMsgTooLongException;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.kernel.support.processor.NettyProcessor;
import io.oss.util.Command;
import io.oss.util.ContentTypes;
import io.oss.util.exception.FileNotFindException;
import io.oss.util.http.HttpResponseCommand;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @Author zhicheng
 * @Date 2021/6/14 3:30 下午
 * @Version 1.0
 */
public class HttpFileDownloadHandler implements NettyProcessor, AutoDependenciesInjector {

    @Inject
    private PullFileServiceWrapper pullFileService;

    private static final Integer DOWNLOAD_THRESHOLD = 30 * 1024 * 1024;

    @Override
    public List<String> getMappingUri() {
        return Collections.singletonList("/oss/download/**");
    }

    @Override
    public Command handle(Command request, HandlerChainContext context) {
        String uri;
        String filePath;

        try {
            uri = request.getHeader().uri();
            filePath = uri.substring("/oss/download".length());
        } catch (Exception e) {
            throw new FileNotFindException();
        }


        File file = new File(filePath);
        long fileLength = pullFileService.getFileLength(filePath);
        if (fileLength > DOWNLOAD_THRESHOLD) {
            throw new PullMsgTooLongException();
        }

        ByteBuffer byteBuffer = pullFileService.pullPartOfFile(filePath, 0L, (int) fileLength);

        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        defaultFullHttpResponse.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES)
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .set(HttpHeaderNames.CONTENT_LENGTH, fileLength)
                .set(HttpHeaderNames.CONTENT_DISPOSITION, HttpHeaderValues.ATTACHMENT + ";" + HttpHeaderValues.FILENAME + "=" + file.getName())
                .set(HttpHeaderNames.CONTENT_TYPE, ContentTypes.APPLICATION_OCTET_STREAM)
                .set(HttpHeaderNames.DATE, new Date().toString())
                .set(HttpHeaderNames.SERVER, "openOss");
        defaultFullHttpResponse.content().writeBytes(byteBuffer);
        return new HttpResponseCommand(defaultFullHttpResponse);
    }

}
