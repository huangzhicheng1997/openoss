package io.oss.server.handler;

import io.netty.util.internal.StringUtil;
import io.oss.file.service.PushFileServiceWrapper;
import io.oss.kernel.Inject;
import io.oss.kernel.support.AutoDependenciesInjector;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.protocol.BodyMsgExtension;
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
    private PushFileServiceWrapper pushFileServiceWrapper;

    public static final String URL_UPLOAD = "/oss/upload";

    public static final String URL_UPLOADED_LENGTH = "/oss/uploadedLength";

    public static final String URL_FINISH_UPLOAD = "/oss/finishUpload";

    @Override
    protected Command handle(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        String uri = request.getHeader().uri();
        switch (uri) {
            case URL_UPLOAD:
                return upload(request, bodyMsgExtension, context);
            case URL_UPLOADED_LENGTH:
                return getUploadedLength(request, bodyMsgExtension, context);
            case URL_FINISH_UPLOAD:
                return finishUploadLength(request, bodyMsgExtension, context);
        }
        return null;
    }

    /**
     * 结束上传
     *
     * @param request
     * @param bodyMsgExtension
     * @param context
     * @return
     */
    private Command finishUploadLength(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        String filePath = bodyMsgExtension.getFilePath();
        if (StringUtil.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be empty!");
        }
        try {
            pushFileServiceWrapper.finish(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 获取上传位点
     *
     * @param request
     * @param bodyMsgExtension
     * @param context
     * @return
     */
    private Command getUploadedLength(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        String filePath = bodyMsgExtension.getFilePath();
        if (StringUtil.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath must not be empty!");
        }
        try {
            Long uploadOffset = pushFileServiceWrapper.getUploadOffset(filePath);
            BodyMsgExtension msgExtension = BodyMsgExtension.Builder.newBuilder().setUploadedLength(uploadOffset)
                    .setContentType(ContentTypes.APPLICATION_JSON).build();
            return CommandBuilder.commonResp(getChannel(context), msgExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 进行上传
     *
     * @param request
     * @param bodyMsgExtension
     * @param context
     * @return
     */
    private Command upload(Command request, BodyMsgExtension bodyMsgExtension, HandlerChainContext context) {
        Long uploadPosition = bodyMsgExtension.getUploadPosition();
        String filePath = bodyMsgExtension.getFilePath();
        ByteBuffer buffer = request.getBody().buffer();
        if (StringUtil.isNullOrEmpty(filePath) || null == buffer || uploadPosition == null) {
            throw new IllegalArgumentException("filepath,buffer,uploadPosition must not be empty!");
        }
        try {
            pushFileServiceWrapper.pushBuffer(filePath, buffer, uploadPosition);
            BodyMsgExtension msgExtension = BodyMsgExtension.Builder.newBuilder()
                    .setContentType(ContentTypes.APPLICATION_JSON)
                    .setUploadedLength(pushFileServiceWrapper.getUploadOffset(filePath))
                    .build();
            return CommandBuilder.commonResp(getChannel(context), msgExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> getMappingUri() {
        return Arrays.asList(URL_UPLOAD, URL_UPLOADED_LENGTH);
    }
}
