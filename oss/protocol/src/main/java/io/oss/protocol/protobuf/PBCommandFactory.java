package io.oss.protocol.protobuf;


import io.oss.protocol.*;

/**
 * @Author zhicheng
 * @Date 2021/4/10 3:53 下午
 * @Version 1.0
 */
public class PBCommandFactory implements CommandFactory {
    @Override
    public CommandType commandType() {
        return CommandType.PROTOBUF;
    }

    @Override
    public Command createCommand(Header header, Body body) {
        PBHeader pbHeader = (PBHeader) header;
        PBBody pbBody = (PBBody) body;
        return PBCommandConvert.convert(pbHeader, pbBody);
    }


}
