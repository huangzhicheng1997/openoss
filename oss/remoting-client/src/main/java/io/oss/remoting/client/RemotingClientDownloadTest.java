package io.oss.remoting.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/5/29 6:08 下午
 * @Version 1.0
 */
public class RemotingClientDownloadTest {
    public static void main(String[] args) {

        OssPullHelp ossPullHelp = new OssPullHelp(
                "",
                new InetSocketAddress("localhost", 9990),
                "/Users/huangzhicheng/Desktop/hzc/upload.txt", "/Users/huangzhicheng/Desktop/hzc/hzc.txt");
        try {
            ossPullHelp.open(true);
            long remotingFileSize = ossPullHelp.getRemotingFileSize();
            ByteBuffer byteBuffer = ossPullHelp.pullPartOfFile(0, (int) remotingFileSize);
            ossPullHelp.writeToLocalFile(byteBuffer);
            ossPullHelp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
