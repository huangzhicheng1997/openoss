package io.oss.protocol;

/**
 * @Author zhicheng
 * @Date 2021/4/10 3:44 下午
 * @Version 1.0
 */
public interface CommandFactory {
    /**
     * 类型，如http，proto buf
     *
     * @return
     */
    CommandType commandType();

    /**
     * 创建 协议报文
     *
     * @param header 协议头
     * @param body   协议主体
     * @return {@link Command}
     */
    Command createCommand(Header header, Body body);
}
