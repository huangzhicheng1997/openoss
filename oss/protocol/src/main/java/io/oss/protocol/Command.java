package io.oss.protocol;

/**
 * 所有协议栈都会被此接口的子类包装，在发送报文前需要调用unwrap方法进行解包
 *
 * @author zhicheng
 * @date 2021-05-05 14:37
 */
public interface Command {

    Header getHeader();

    Body getBody();

    Object unWrap();


}
