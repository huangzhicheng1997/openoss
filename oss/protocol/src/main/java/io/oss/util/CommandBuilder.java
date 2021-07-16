package io.oss.util;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;
import io.oss.util.exception.ExceptionCode;
import io.oss.util.http.*;
import io.oss.util.protobuf.PBBody;
import io.oss.util.protobuf.PBCommandFactory;
import io.oss.util.protobuf.PBHeader;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhicheng
 * @date 2021-05-11 18:18
 */
public class CommandBuilder {

    private static final CommandFactory pbCommandFactory = new PBCommandFactory();

    private static final AtomicInteger counter = new AtomicInteger(0);


    public static Command commonResp(Channel channel, BodyMsgExtension extension) {
        return fullResponse(channel, extension, null);
    }

    public static Command fullResponse(Channel channel, BodyMsgExtension extension, ByteBuffer buffer) {
        Gson gson = new Gson();
        PBHeader pbHeader = new PBHeader();
        PBBody pbBody = new PBBody();
        pbBody.setResp(gson.toJson(extension));
        pbBody.putBuffer(buffer);
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }

    public static Command errorMsgCommand(Channel channel, ExceptionCode exceptionCode, String errorMsg) {
        if (isHttp(channel)) {
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, exceptionCode.getResponseStatus());
            return fullHttpResponse(defaultFullHttpResponse);
        } else {
            BodyMsgExtension msgExtension = BodyMsgExtension.Builder.newBuilder()
                    .setContentType(ContentTypes.APPLICATION_JSON)
                    .setErrorMsg(StringUtil.isNullOrEmpty(errorMsg) ? exceptionCode.getMsg() : errorMsg)
                    .setErrorCode(exceptionCode.getCode()).build();
            return fullResponse(channel, msgExtension, null);
        }
    }


    public static Command replaceSeq(Channel channel, Command command, Integer seq) {
        if (!isHttp(channel)) {
            PBHeader pbHeader = new PBHeader();
            pbHeader.setUri(command.getHeader().uri());
            pbHeader.setSeq(seq);
            pbHeader.setAccessToken(command.getHeader().accessToken());
            PBBody pbBody = new PBBody();
            pbBody.setResp(command.getBody().resp());
            pbBody.putBuffer(command.getBody().buffer());
            return pbCommandFactory.createCommand(pbHeader, pbBody);
        }
        return command;
    }

    public static Command commonRequest(String uri, String accessToken, BodyMsgExtension extension) {
        PBHeader pbHeader = new PBHeader();
        pbHeader.setAccessToken(accessToken);
        //最大值就是2的n次方减一，直接按位与
        pbHeader.setSeq(counter.getAndIncrement() & Integer.MAX_VALUE);
        pbHeader.setUri(uri);
        PBBody pbBody = new PBBody();
        Gson gson = new Gson();
        pbBody.setResp(gson.toJson(extension));
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }

    public static Command fullRequest(String uri, String accessToken, BodyMsgExtension extension, ByteBuffer buffer) {
        PBHeader pbHeader = new PBHeader();
        pbHeader.setAccessToken(accessToken);
        //最大值就是2的n次方减一，直接按位与
        pbHeader.setSeq(counter.getAndIncrement() & Integer.MAX_VALUE);
        pbHeader.setUri(uri);
        PBBody pbBody = new PBBody();
        pbBody.putBuffer(buffer);
        Gson gson = new Gson();
        pbBody.setResp(gson.toJson(extension));
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }

    public static Command fullHttpResponse(DefaultFullHttpResponse response) {
        return new HttpResponseCommand(response);
    }

    private static boolean isHttp(Channel channel) {
        return HttpChannelRecord.isHttpChannel(channel);
    }
}
