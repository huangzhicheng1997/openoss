package io.oss.server;

import io.oss.kernel.core.PBKernelStarter;

/**
 * @Author zhicheng
 * @Date 2021/5/24 8:01 下午
 * @Version 1.0
 */
public class OssServer {
    public static void main(String[] args) {
        PBKernelStarter runner = new PBKernelStarter("io.oss.acl," +
                "io.oss.kernel," +
                "io.oss.protocol," +
                "io.oss.server,"+
                "io.oss.file.service");
        runner.run();
    }
}
