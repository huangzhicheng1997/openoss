package io.oss.server.handler;

import io.netty.util.internal.StringUtil;
import io.oss.file.service.IndexablePushFileService;
import io.oss.kernel.Inject;
import io.oss.kernel.support.AutoDependenciesInjector;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.protocol.BodyDta;
import io.oss.protocol.Command;
import io.oss.protocol.CommandBuilder;
import io.oss.protocol.ContentTypes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * @Author zhicheng
 * @Date 2021/5/30 1:31 下午
 * @Version 1.0
 */
public class UploadHandler extends AbstractNettyProcessorHandler implements AutoDependenciesInjector {

    @Inject
    private IndexablePushFileService indexablePushFileService;

    public static final String URL_UPLOAD = "/oss/upload";

    public static final String URL_UPLOADED_LENGTH = "/oss/uploadedLength";

    public static final String URL_FINISH_UPLOAD = "/oss/finishUpload";

    @Override
    protected Command handle(Command request, BodyDta bodyDta, HandlerChainContext context) {
        String uri = request.getHeader().uri();
        switch (uri) {
            case URL_UPLOAD:
                return upload(request, bodyDta, context);
            case URL_UPLOADED_LENGTH:
                return getUploadedLength(request, bodyDta, context);
            case URL_FINISH_UPLOAD:
                return finishUploadLength(request, bodyDta, context);
        }
        return null;
    }

    /**
     * 结束上传
     *
     * @param request
     * @param bodyDta
     * @param context
     * @return
     */
    private Command finishUploadLength(Command request, BodyDta bodyDta, HandlerChainContext context) {
        String filePath = bodyDta.getFilePath();
        if (StringUtil.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be empty!");
        }
        try {
            indexablePushFileService.finish(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 获取上传位点
     *
     * @param request
     * @param bodyDta
     * @param context
     * @return
     */
    private Command getUploadedLength(Command request, BodyDta bodyDta, HandlerChainContext context) {
        String filePath = bodyDta.getFilePath();
        if (StringUtil.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be empty!");
        }
        try {
            Long uploadOffset = indexablePushFileService.getUploadOffset(filePath);
            BodyDta msgExtension = BodyDta.Builder.newBuilder().setUploadedLength(uploadOffset)
                    .setContentType(ContentTypes.APPLICATION_JSON).build();
            return CommandBuilder.commonRespCommand(getChannel(context), msgExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 进行上传
     *
     * @param request
     * @param bodyDta
     * @param context
     * @return
     */
    private Command upload(Command request, BodyDta bodyDta, HandlerChainContext context) {
        Long uploadPosition = bodyDta.getUploadPosition();
        String filePath = bodyDta.getFilePath();
        ByteBuffer buffer = request.getBody().buffer();
        if (StringUtil.isNullOrEmpty(filePath) || null == buffer || uploadPosition == null) {
            throw new IllegalArgumentException("filepath,buffer,uploadPosition must not be empty!");
        }
        try {
            indexablePushFileService.pushBuffer(filePath, buffer, uploadPosition);
            BodyDta msgExtension = BodyDta.Builder.newBuilder()
                    .setContentType(ContentTypes.APPLICATION_JSON)
                    .setUploadedLength(indexablePushFileService.getUploadOffset(filePath))
                    .build();
            return CommandBuilder.commonRespCommand(getChannel(context), msgExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> getMappingUri() {
        return Arrays.asList(URL_UPLOAD, URL_UPLOADED_LENGTH);
    }
}
