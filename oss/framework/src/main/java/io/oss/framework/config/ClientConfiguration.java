package io.oss.framework.config;

public class ClientConfiguration {
    private String serverHostName="localhost";
    private Integer serverPort=9200;
    private Integer uploadThreadPoolSize=3;

    public String getServerHostName() {
        return serverHostName;
    }

    public void setServerHostName(String serverHostName) {
        this.serverHostName = serverHostName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getUploadThreadPoolSize() {
        return uploadThreadPoolSize;
    }

    public void setUploadThreadPoolSize(Integer uploadThreadPoolSize) {
        this.uploadThreadPoolSize = uploadThreadPoolSize;
    }
}
