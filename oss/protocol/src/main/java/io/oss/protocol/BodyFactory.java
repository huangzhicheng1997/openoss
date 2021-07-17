package io.oss.protocol;

/**
 * @Author zhicheng
 * @Date 2021/5/29 6:15 下午
 * @Version 1.0
 */
public class BodyFactory {

    private final ThreadLocal<BodyMsgExtension> msgExtension;

    {
        msgExtension = ThreadLocal.withInitial(BodyMsgExtension::new);
    }

    public static BodyFactory getInstance() {
        return Instance.BODY_FACTORY;
    }

    public BodyMsgExtension getFileLength(String filePath) {
        msgExtension.get().setFilePath(filePath);
        BodyMsgExtension result = msgExtension.get();
        msgExtension.remove();
        return result;
    }

    public BodyMsgExtension pullPartOfFile(String filePath, long position, int length) {
        BodyMsgExtension msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        msgExtension.setPullPosition(position);
        msgExtension.setPullLength(length);
        this.msgExtension.remove();
        return msgExtension;
    }

    /**
     * 上传获取初始偏移
     */
    public BodyMsgExtension getUploadedOffset(String filePath) {
        BodyMsgExtension msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        this.msgExtension.remove();
        return msgExtension;
    }

    public BodyMsgExtension upload(String filePath, Long position) {
        BodyMsgExtension msgExtension = this.msgExtension.get();
        msgExtension.setFilePath(filePath);
        msgExtension.setUploadPosition(position);
        this.msgExtension.remove();
        return msgExtension;
    }


    private static class Instance {
        private static final BodyFactory BODY_FACTORY = new BodyFactory();
    }
}
