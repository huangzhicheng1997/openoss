package io.oss.util;

/**
 * @Author zhicheng
 * @Date 2021/6/23 8:39 下午
 * @Version 1.0
 */
public enum ConfigurationEnum {
    PROCESSOR_COMMON_THREAD_POOL_SIZE("netty.processor.common.handler.threads"),
    PROCESSOR_TIMEOUT("netty.processor.timeout"),
    ;
    String key;

    ConfigurationEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
