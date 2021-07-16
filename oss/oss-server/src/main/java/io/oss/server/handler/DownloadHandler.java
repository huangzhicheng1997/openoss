package io.oss.server.handler;

import io.oss.file.service.PullFileServiceWrapper;
import io.oss.kernel.Inject;
import io.oss.kernel.spi.plugins.Component;
import io.oss.kernel.spi.plugins.FindDependenciesComponent;
import io.oss.kernel.support.AutoDependenciesInjector;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.util.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author zhicheng
 * @Date 2021/5/24 8:03 下午
 * @Version 1.0
 */
public class DownloadHandler extends AbstractNettyProcessorHandler implements AutoDependenciesInjector {

    @Inject
    private PullFileServiceWrapper pullFileService;

    public static final String GET_FILE_LENGTH = "/oss/download/getFileLength";

    public static final String PULL_FILE = "/oss/download/pull";

    @Override
    public List<String> getMappingUri() {
        return Arrays.asList(GET_FILE_LENGTH, PULL_FILE);
    }

    @Override
    protected Command handle(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        String uri = request.getHeader().uri();
        switch (uri) {
            case GET_FILE_LENGTH:
                return processGetLength(request, bodyMsgExtension, context);
            case PULL_FILE:
                return processPullFile(request, bodyMsgExtension, context);
        }

        return null;
    }

    private Command processPullFile(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        String filePath = bodyMsgExtension.getFilePath();
        Integer pullLength = bodyMsgExtension.getPullLength();
        Long pullPosition = bodyMsgExtension.getPullPosition();
        if (null == filePath || null == pullLength || null == pullPosition) {
            throw new IllegalArgumentException("filePath,pullLength,pullPosition can not be null!");
        }
        long fileLength = pullFileService.getFileLength(filePath);

        ByteBuffer byteBuffer = pullFileService.pullPartOfFile(filePath, pullPosition, pullLength);

        BodyMsgExtension msgExtension = BodyMsgExtension.Builder.newBuilder()
                .setContentType(ContentTypes.APPLICATION_OCTET_STREAM)
                .setPullLength(pullLength)
                .setPullPosition(pullPosition)
                .setFileLength(fileLength)
                .setLastModify(new Date(new File(filePath).lastModified()).toString())
                .build();

        return CommandBuilder.fullResponse(getChannel(context), msgExtension, byteBuffer);
    }


    private Command processGetLength(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        if (bodyMsgExtension.getFilePath() == null) {
            throw new IllegalArgumentException("file path can not be null!");
        }
        long fileLength = pullFileService.getFileLength(bodyMsgExtension.getFilePath());

        BodyMsgExtension msgExtension = BodyMsgExtension.Builder.newBuilder()
                .setContentType(ContentTypes.APPLICATION_JSON)
                .setFileLength(fileLength).build();

        return CommandBuilder.commonResp(getChannel(context), msgExtension);
    }

}
