package io.oss.framework.config;

/**
 * @author zhicheng
 * @date 2021-01-22 14:53
 */
public class NettyConfiguration {
    private Integer acceptors = 1;
    private Integer ioWorkers = 4;
    private Integer handlerWorks = 4;
    private Integer so_sendBuffer = 1024 * 1024;
    private Integer so_receiveBuffer = 1024 * 1024;
    private Integer so_backlog = 1024;
    private Integer maxFrameLength = 1024*1024;

    public Integer getMaxFrameLength() {
        return maxFrameLength;
    }

    public void setMaxFrameLength(Integer maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

    public Integer getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(Integer acceptors) {
        this.acceptors = acceptors;
    }

    public Integer getIoWorkers() {
        return ioWorkers;
    }

    public void setIoWorkers(Integer ioWorkers) {
        this.ioWorkers = ioWorkers;
    }

    public Integer getHandlerWorks() {
        return handlerWorks;
    }

    public void setHandlerWorks(Integer handlerWorks) {
        this.handlerWorks = handlerWorks;
    }

    public Integer getSo_sendBuffer() {
        return so_sendBuffer;
    }

    public void setSo_sendBuffer(Integer so_sendBuffer) {
        this.so_sendBuffer = so_sendBuffer;
    }

    public Integer getSo_receiveBuffer() {
        return so_receiveBuffer;
    }

    public void setSo_receiveBuffer(Integer so_receiveBuffer) {
        this.so_receiveBuffer = so_receiveBuffer;
    }

    public Integer getSo_backlog() {
        return so_backlog;
    }

    public void setSo_backlog(Integer so_backlog) {
        this.so_backlog = so_backlog;
    }
}
