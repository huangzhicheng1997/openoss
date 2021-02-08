package io.oss.framework.remoting.protocol;

/**
 * @author zhicheng
 * @date 2021-01-22 10:00
 */
public class RequestCode {
    /**
     * 上传请求
     */
    public static final byte UPLOAD_REQUEST_START = 1;
    public static final byte UPLOAD_RESPONSE_START = 2;

    /**
     * 上传文件
     */
    public static final byte UPLOAD_REQUEST_FILE = 3;
    public static final byte UPLOAD_RESPONSE_FILE = 4;

    /**
     * 复位重传
     */
    public static final byte UPLOAD_RESET=5;

    /**
     * 拒绝上传（不支持相同文件名文件同时上传，减少同步带来的性能损耗）
     */
    public static final byte REJECT_UPLOAD_FILE_ALREADY_EXISTED=6;

}
