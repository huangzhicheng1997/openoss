package io.oss.remoting.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @Author zhicheng
 * @Date 2021/6/1 3:27 下午
 * @Version 1.0
 */
public class RemotingClientUploadTest {

    public static void main(String[] args) throws InterruptedException {
        //1.拿到服务器文件位置
        //

        OssPushHelp ossPushHelp = new OssPushHelp("xx", new InetSocketAddress("localhost", 9990));
        Long uploadedOffset = ossPushHelp.getUploadedOffset("/Users/huangzhicheng/Desktop/hzc/upload.txt");
        byte[] bytes = "https://www.hentais.tube/tvshows/tiny-evil/".getBytes();
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        Long uploaded = ossPushHelp.upload("/Users/huangzhicheng/Desktop/hzc/upload.txt", wrap, uploadedOffset+1);
        System.out.println(uploaded);

    }
}
