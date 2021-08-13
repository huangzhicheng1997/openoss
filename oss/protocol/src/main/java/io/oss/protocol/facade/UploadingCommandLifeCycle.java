package io.oss.protocol.facade;

import io.netty.channel.Channel;
import io.oss.protocol.Command;

import java.nio.ByteBuffer;

/**
 * 上传流程的整个生命周期，所需要的报文。
 * 关于响应报文，只封装正确的返回报文，异常报文由异常流处理器进行处理
 *
 * @author zhicheng
 * @date 2021-07-17 15:16
 */
public interface UploadingCommandLifeCycle {

    /**
     * 客户端获取下一次上传的位点
     *
     * @param serverFilePath 服务器上文件的绝对路径
     * @param uri
     * @param accessToken
     * @return 报文
     */
    Command obtainNextUploadOffset(String serverFilePath, String uri, String accessToken);

    /**
     * {@link UploadingCommandLifeCycle#obtainNextUploadOffset(String, String, String)} 的服务的响应
     *
     * @param nextOffset <p>
     *                   * 已上传完成的文件 0
     *                   * 已删除的文件    -1
     *                   * 不存在的文件    -2
     * @param channel
     * @return 报文
     */
    Command obtainNextUploadOffsetAck(Long nextOffset, Channel channel);

    /**
     * 客户端推送 bytes
     *
     * @param serverFilePath 服务器上文件的绝对路径
     * @param byteBuffer     buffer
     * @param offset         上传位点
     * @param fileFullLength 完整文件大小（字节）
     * @param uri
     * @param accessToken
     * @return 报文
     */
    Command pushStreamFromOffset(String serverFilePath, ByteBuffer byteBuffer, long offset,
                                 long fileFullLength, String uri, String accessToken);

    /**
     * {@link UploadingCommandLifeCycle#pushStreamFromOffset(String, ByteBuffer, long, long, String, String)} 服务端的响应
     *
     * @param nextOffset <p>
     *                   * 已上传完成的文件 0
     *                   * 已删除的文件    -1
     *                   * 不存在的文件    -2
     * @param channel
     * @param currentLength
     * @return 报文
     */
    Command pushAck(Long nextOffset, Channel channel, Long currentLength);

    /**
     * 客户端请求完成一次上传任务
     *
     * @param serverFilePath 服务器上文件的绝对路径
     * @param uri
     * @param accessToken
     * @return 报文
     */
    Command finishUpload(String serverFilePath, String uri, String accessToken);

    /**
     * {@link UploadingCommandLifeCycle#finishUpload(String, String, String)} 服务端的ack
     *
     * @param result <p>
     *               * 上传完成       0
     *               * 已被删除       -1
     *               * 未存在过的文件  -2
     * @param channel
     * @return 报文
     */
    Command finishUploadAck(Integer result, Channel channel);


}
