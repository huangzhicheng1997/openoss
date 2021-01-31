package io.oss.framework.config;

import java.io.File;

/**
 * @author zhicheng
 * @date 2021-01-22 14:42
 */
public class ServerConfiguration {
    private String fileStorePath = /*System.getProperty("user.home") + File.separator + "tempDir";*/"/Users/huangzhicheng/Desktop/tes";
    private Integer port = 9200;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getFileStorePath() {
        return fileStorePath;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }
}
