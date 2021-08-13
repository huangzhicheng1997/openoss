package io.oss.protocol;

/**
 * 交互协议的统一抽象，包含http协议以及基于protoBuffer的私有协议栈。
 *
 * @author zhicheng
 * @date 2021-05-05 14:37
 */
public interface Command {

    /**
     * 报文的头部
     *
     * @return {@link Header}
     */
    Header getHeader();

    /**
     * 报文的主体
     *
     * @return {@link Body}
     */
    Body getBody();

    /**
     * 解包原始协议，例如经过解包可获取原始的java版ProtoBuffer的对象
     *
     * @return 原始报文对象
     */
    Object unWrap();


}
