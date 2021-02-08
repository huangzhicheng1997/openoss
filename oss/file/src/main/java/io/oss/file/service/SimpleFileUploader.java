package io.oss.file.service;

import io.oss.framework.config.ClientConfiguration;
import io.oss.framework.config.NettyConfiguration;
import io.oss.framework.remoting.FileClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;

/**
 * @author zhicheng
 * @date 2021-02-02 10:13
 */
public class SimpleFileUploader {
    private FileClient fileClient;

    public SimpleFileUploader(ClientConfiguration clientConfiguration) {
        fileClient = new FileClient(new NettyConfiguration(), clientConfiguration);
    }

    public void uploadFile(InputStream inputStream, String fileName) throws IOException {
        int fileLength = inputStream.available();
        byte[] data = new byte[fileLength];

        for (; ; ) {
            int read = inputStream.read(data);
            if (read == -1) {
                break;
            }
        }
        /*FileCommandFactory.RequestFactory.uploadFileRequest(fileName, fileLength, ByteBuffer.wrap())*/
    }


}
