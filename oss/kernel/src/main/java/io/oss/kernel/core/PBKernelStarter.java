package io.oss.kernel.core;

import io.oss.protocol.CodecHelp;
import io.oss.protocol.protobuf.ProtobufCodecHelp;
import io.oss.protocol.CommandFactory;
import io.oss.protocol.protobuf.PBCommandFactory;

/**
 * @Author zhicheng
 * @Date 2021/4/12 7:29 下午
 * @Version 1.0
 */
public class PBKernelStarter extends KernelStarter {


    public PBKernelStarter(String packages) {
        super(packages);
    }

    @Override
    protected CommandFactory createProtocolCommandFactory0() {
        return new PBCommandFactory();
    }

    @Override
    protected CodecHelp codecHelp() {
        return new ProtobufCodecHelp();
    }
}
