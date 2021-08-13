package io.oss.protocol.facade;

import io.netty.channel.Channel;
import io.oss.protocol.BodyDta;
import io.oss.protocol.Command;
import io.oss.protocol.CommandBuilder;
import io.oss.util.util.ValidateUtil;

import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-07-17 15:54
 */
public class PBUploadingCommandLifeCycle implements UploadingCommandLifeCycle {

    @Override
    public Command obtainNextUploadOffset(String serverFilePath, String uri, String accessToken) {
        ValidateUtil.assertStringNotBlank(serverFilePath, uri, accessToken);
        return CommandBuilder.commonRequest(uri, accessToken, BodyDta.Builder.newBuilder().setFilePath(serverFilePath).build());
    }

    @Override
    public Command obtainNextUploadOffsetAck(Long nextOffset, Channel channel) {
        ValidateUtil.assertNotBlankEmptyAndNull(nextOffset);
        return CommandBuilder.commonRespCommand(channel, BodyDta.Builder.newBuilder().setNextPushOffset(nextOffset).build());
    }

    @Override
    public Command pushStreamFromOffset(String serverFilePath, ByteBuffer byteBuffer,
                                        long offset, long fileFullLength, String uri,
                                        String accessToken) {
        ValidateUtil.assertNotBlankEmptyAndNull(serverFilePath, byteBuffer, offset, fileFullLength, uri, accessToken);
        return CommandBuilder.fullRequest(uri, accessToken,
                BodyDta.Builder.newBuilder().setFilePath(serverFilePath).setNextPushOffset(offset).setFileLength(fileFullLength).build(),
                byteBuffer);
    }

    @Override
    public Command pushAck(Long nextOffset, Channel channel, Long currentLength) {
        ValidateUtil.assertNotBlankEmptyAndNull(nextOffset, channel);
        return CommandBuilder.commonRespCommand(channel,
                BodyDta.Builder.newBuilder().setNextPushOffset(nextOffset).setCurrentLength(currentLength).build());
    }

    @Override
    public Command finishUpload(String serverFilePath, String uri, String accessToken) {
        ValidateUtil.assertNotBlankEmptyAndNull(serverFilePath, uri, accessToken);
        return CommandBuilder.commonRequest(uri, accessToken, BodyDta.Builder.newBuilder().setFilePath(serverFilePath).build());
    }

    @Override
    public Command finishUploadAck(Integer result, Channel channel) {
        ValidateUtil.assertNotBlankEmptyAndNull(result, channel);
        return CommandBuilder.commonRespCommand(channel, BodyDta.Builder.newBuilder().setFinishUploadAck(result).build());
    }
}
