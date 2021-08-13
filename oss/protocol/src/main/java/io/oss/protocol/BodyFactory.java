package io.oss.protocol;

/**
 * @Author zhicheng
 * @Date 2021/5/29 6:15 下午
 * @Version 1.0
 */
public class BodyFactory {

    private final ThreadLocal<BodyDta> msgExtension;

    {
        msgExtension = ThreadLocal.withInitial(BodyDta::new);
    }

    public static BodyFactory getInstance() {
        return Instance.BODY_FACTORY;
    }

    public BodyDta getFileLength(String filePath) {
        msgExtension.get().setFilePath(filePath);
        BodyDta result = msgExtension.get();
        msgExtension.remove();
        return result;
    }

    public BodyDta pullPartOfFile(String filePath, long position, int length) {
        BodyDta msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        msgExtension.setPullPosition(position);
        msgExtension.setPullLength(length);
        this.msgExtension.remove();
        return msgExtension;
    }

    /**
     * 上传获取初始偏移
     */
    public BodyDta getUploadedOffset(String filePath) {
        BodyDta msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        this.msgExtension.remove();
        return msgExtension;
    }

    public BodyDta upload(String filePath, Long position) {
        BodyDta msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        msgExtension.setUploadPosition(position);
        this.msgExtension.remove();
        return msgExtension;
    }


    private static class Instance {
        private static final BodyFactory BODY_FACTORY = new BodyFactory();
    }
}
