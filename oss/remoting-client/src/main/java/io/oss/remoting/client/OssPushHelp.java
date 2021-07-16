package io.oss.remoting.client;

import io.oss.util.BodyFactory;
import io.oss.util.BodyMsgExtension;
import io.oss.util.Command;
import io.oss.util.CommandBuilder;
import io.oss.util.protobuf.PBCommandFactory;
import io.oss.util.protobuf.ProtobufCodecHelp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * socketAddress必须使用索引服务的地址，由索引服务进行转发。目的是通过索引服务进行并发上传控制，以及上传进度检测
 *
 * @Author zhicheng
 * @Date 2021/5/30 1:30 下午
 * @Version 1.0
 */
public class OssPushHelp {

    private String accessToken;

    private SocketAddress socketAddress;

    private RemotingClient remotingClient = new RemotingClient(new PBCommandFactory(), new ProtobufCodecHelp());

    private static final String URL_UPLOADED_OFFSET = "/oss/uploadedLength";

    public static final String URL_UPLOAD = "/oss/upload";


    public OssPushHelp(String accessToken, SocketAddress socketAddress) {
        this.accessToken = accessToken;
        this.socketAddress = socketAddress;
    }


    public Long getUploadedOffset(String filePath) throws InterruptedException {
        Command command = CommandBuilder.commonRequest(URL_UPLOADED_OFFSET, accessToken, BodyFactory.getInstance().getUploadedOffset(filePath));
        Command resp = remotingClient.request(command, socketAddress).getSync();
        BodyMsgExtension msgExtension = resultHandle(resp);
        return msgExtension.getUploadedLength();
    }

    public Long upload(String filePath, ByteBuffer buffer, Long position) throws InterruptedException {
        Command command = CommandBuilder.fullRequest(URL_UPLOAD, accessToken, BodyFactory.getInstance().upload(filePath, position), buffer);
        Command resp = remotingClient.request(command, socketAddress).getSync();
        BodyMsgExtension msgExtension = resultHandle(resp);
        return msgExtension.getUploadedLength();
    }

    public void finish(String filPath) {
    }

    private BodyMsgExtension resultHandle(Command result) {
        BodyMsgExtension msgExtension = BodyMsgExtension.fromJson(result.getBody().resp());
        if (null != msgExtension && null != msgExtension.getErrorMsg()) {
            throw new RemotingClientException(msgExtension.getErrorMsg());
        }
        return msgExtension;
    }
}