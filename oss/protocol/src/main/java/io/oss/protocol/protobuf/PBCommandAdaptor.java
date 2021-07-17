package io.oss.protocol.protobuf;

import com.google.protobuf.ByteString;
import io.netty.util.internal.StringUtil;
import io.oss.protocol.Body;
import io.oss.protocol.Command;
import io.oss.protocol.Header;
import io.oss.protocol.exception.ProtocolException;
import io.oss.protocol.protobuf.pbfile.ProtoBufProtocol;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 * @author zhicheng
 * @date 2021-05-05 15:20
 */
public class PBCommandAdaptor implements Command {
    private ProtoBufProtocol.FileCommand fileCommand;

    public PBCommandAdaptor(ProtoBufProtocol.FileCommand fileCommand) {
        this.fileCommand = fileCommand;
    }

    public PBCommandAdaptor(PBHeader header, PBBody body) {
        if (body == null || header == null) {
            throw new ProtocolException("protocol body or header can not be empty");
        }
        ProtoBufProtocol.FileCommand.Body.Builder bodyBuilder = ProtoBufProtocol.FileCommand.Body.newBuilder();
        if (body.buffer() != null) {
            bodyBuilder.setBuffer(ByteString.copyFrom(body.buffer()));
        }
        ProtoBufProtocol.FileCommand.Header.Builder headerBuilder = ProtoBufProtocol.FileCommand.Header.newBuilder();
        if (!StringUtil.isNullOrEmpty(header.accessToken())) {
            headerBuilder.setAccessToken(header.accessToken());
        }
        if (null != header.seq()) {
            headerBuilder.setSeq(header.seq());
        }
        if (!StringUtil.isNullOrEmpty(header.uri())) {
            try {
                headerBuilder.setUri(URLDecoder.decode(header.uri(), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        if (!StringUtil.isNullOrEmpty(body.resp())) {
            bodyBuilder.setResp(body.resp());
        }
        this.fileCommand = ProtoBufProtocol.FileCommand.newBuilder()
                .addBody(bodyBuilder)
                .addHeader(headerBuilder).build();
    }


    @Override
    public Header getHeader() {
        ProtoBufProtocol.FileCommand.Header header = fileCommand.getHeader(0);
        PBHeader pbHeader = new PBHeader();
        pbHeader.setAccessToken(header.getAccessToken());
        pbHeader.setSeq(header.getSeq());
        pbHeader.setUri(header.getUri());
        return pbHeader;
    }

    @Override
    public Body getBody() {
        ProtoBufProtocol.FileCommand.Body body = fileCommand.getBody(0);
        PBBody pbBody = new PBBody();
        pbBody.setResp(body.getResp());
        ByteString bytes = body.getBuffer();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.size());
        bytes.copyTo(buffer);
        buffer.flip();
        pbBody.putBuffer(buffer);
        return pbBody;
    }

    @Override
    public Object unWrap() {
        return this.fileCommand;
    }
}
