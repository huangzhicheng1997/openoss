package io.oss.protocol;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;
import io.oss.protocol.exception.ExceptionCode;
import io.oss.protocol.http.HttpChannelRecord;
import io.oss.protocol.http.HttpResponseCommand;
import io.oss.protocol.protobuf.PBBody;
import io.oss.protocol.protobuf.PBCommandFactory;
import io.oss.protocol.protobuf.PBHeader;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Command}工厂, 包含http协议的请求响应包装方法，以及ProtoBuffer的私有协议的包装方法
 *
 * @author zhicheng
 * @date 2021-05-11 18:18
 */
public class CommandBuilder {

    private static final CommandFactory pbCommandFactory = new PBCommandFactory();

    /**
     * 请求序列号，发号器
     */
    private static final AtomicInteger requestSeqCounter = new AtomicInteger(0);

    /**
     * protoBuffer普通响应，不包含流数据
     *
     * @param channel   {@link Channel}
     * @param extension {@link BodyDta}
     * @return {@link Command}
     */
    public static Command commonRespCommand(Channel channel, BodyDta extension) {
        return fullRespCommand(channel, extension, null);
    }

    /**
     * protoBuffer完整的报文数据，包含流数据
     *
     * @param channel   {@link Channel}
     * @param extension {@link BodyDta}
     * @param buffer    携带字节流的数据
     * @return {@link Command}
     */
    public static Command fullRespCommand(Channel channel, BodyDta extension, ByteBuffer buffer) {
        Gson gson = new Gson();
        PBHeader pbHeader = new PBHeader();
        PBBody pbBody = new PBBody();
        pbBody.setResp(gson.toJson(extension));
        pbBody.putBuffer(buffer);
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }


    /**
     * http和tcp协议的异常报文统一封装<br>
     * 其中errorMsg可为null。为null则默认取exceptionCode中的异常信息
     *
     * @param channel       {@link Channel}
     * @param exceptionCode {@link ExceptionCode}
     * @param errorMsg      额外的错误信息
     * @return {@link Command}
     */
    public static Command errorMsgCommand(Channel channel, ExceptionCode exceptionCode, String errorMsg) {
        if (isHttp(channel)) {
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, exceptionCode.getResponseStatus());
            return fullHttpResponseCommand(defaultFullHttpResponse);
        } else {
            BodyDta msgExtension = BodyDta.Builder.newBuilder()
                    .setContentType(ContentTypes.APPLICATION_JSON)
                    .setErrorMsg(StringUtil.isNullOrEmpty(errorMsg) ? exceptionCode.getMsg() : errorMsg)
                    .setErrorCode(exceptionCode.getCode()).build();
            return fullRespCommand(channel, msgExtension, null);
        }
    }

    /**
     * 目的是对报文做序列号注入，以标识为那个会话的响应报文
     *
     * @param channel {@link Channel}
     * @param command {@link Command}
     * @param seq     报文序号
     * @return {@link Command}
     */
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

    /**
     * protoBuffer请求报文封装，不包含流数据
     *
     * @param uri         请求路径
     * @param accessToken 认证令牌
     * @param body        请求体
     * @return {@link Command}
     */
    public static Command commonRequest(String uri, String accessToken, BodyDta body) {
        PBHeader pbHeader = new PBHeader();
        pbHeader.setAccessToken(accessToken);
        //最大值就是2的n次方减一，直接按位与
        pbHeader.setSeq(requestSeqCounter.getAndIncrement() & Integer.MAX_VALUE);
        pbHeader.setUri(uri);
        PBBody pbBody = new PBBody();
        Gson gson = new Gson();
        pbBody.setResp(gson.toJson(body));
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }


    /**
     * 附带流数据的请求报文，例如上传
     *
     * @param uri         请求路径
     * @param accessToken 认证令牌
     * @param bodyDta     请求体
     * @param buffer      buffer
     * @return {@link Command}
     */
    public static Command fullRequest(String uri, String accessToken, BodyDta bodyDta, ByteBuffer buffer) {
        PBHeader pbHeader = new PBHeader();
        pbHeader.setAccessToken(accessToken);
        //最大值就是2的n次方减一，直接按位与
        pbHeader.setSeq(requestSeqCounter.getAndIncrement() & Integer.MAX_VALUE);
        pbHeader.setUri(uri);
        PBBody pbBody = new PBBody();
        pbBody.putBuffer(buffer);
        Gson gson = new Gson();
        pbBody.setResp(gson.toJson(bodyDta));
        return pbCommandFactory.createCommand(pbHeader, pbBody);
    }

    /**
     * http协议，Command封装
     *
     * @param response http响应报文
     * @return {@link Command}
     */
    public static Command fullHttpResponseCommand(DefaultFullHttpResponse response) {
        return new HttpResponseCommand(response);
    }


    /**
     * 是否为http协议
     *
     * @param channel {@link Channel}
     * @return http协议的连接为true，tcp私有协议连接为false
     */
    private static boolean isHttp(Channel channel) {
        return HttpChannelRecord.isHttpChannel(channel);
    }
}
