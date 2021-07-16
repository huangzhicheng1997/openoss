package io.oss.kernel.environment;

/**
 * @Author zhicheng
 * @Date 2021/4/10 6:39 下午
 * @Version 1.0
 */
public class KernelEnvironment extends NamedEnvironment {
    public static final String PLATFORM_TYPE = "platform.type";
    public static final String BOSS_THREADS = "netty.boss.threads";
    public static final String WORKER_THREADS = "netty.worker.threads";
    public static final String HANDLER_THREADS = "netty.handler.threads";
    public static final String SERVER_PORT = "server.port";
    public static final String IDLE_SECONDS_TIME = "idle.time";

    public static final String KernelEnvironmentName = "kernel";

    public KernelEnvironment() {
        super(KernelEnvironmentName);
    }
}
