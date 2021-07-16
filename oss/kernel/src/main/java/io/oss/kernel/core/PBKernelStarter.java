package io.oss.kernel.core;

import io.oss.util.CodecHelp;
import io.oss.util.protobuf.ProtobufCodecHelp;
import io.oss.util.CommandFactory;
import io.oss.util.protobuf.PBCommandFactory;

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
