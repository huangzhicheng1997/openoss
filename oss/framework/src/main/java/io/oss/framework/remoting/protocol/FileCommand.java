package io.oss.framework.remoting.protocol;


import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-01-18 14:23
 */
public class FileCommand {

    /**
     * 请求类型
     */
    private byte protocolType;

    /**
     * 请求序号
     */
    private int requestSeq;

    private int fileDataLength;

    private FileData fileData;


    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(getCommandLength()+1 + Integer.BYTES * 2 + fileData.getObjectSize());
        buffer.putInt(getCommandLength());
        buffer.put(protocolType);
        buffer.putInt(requestSeq);
        buffer.putInt(fileDataLength);
        buffer.put(fileData.encodeToBuffer());
        buffer.flip();
        return buffer;
    }

    public int getCommandLength() {
        return Byte.BYTES + Integer.BYTES * 2 + fileData.getObjectSize();
    }

    /**
     * 解码分为两块，header部分和body部分
     *
     * @param in
     * @return
     */
    public static FileCommand decode(ByteBuffer in) {
        FileCommand fileCommand = new FileCommand();
        //先解码header
        int commandLength = in.getInt();
        byte protocolType = in.get();
        int requestSeq = in.getInt();
        int fileDataLength = in.getInt();
        fileCommand.setProtocolType(protocolType);
        fileCommand.setRequestSeq(requestSeq);
        fileCommand.setFileDataLength(fileDataLength);

        decodeBody(in, protocolType, fileCommand);

        return fileCommand;
    }

    /*
     * 解码报文body部分,不需要Body部分的不需要添加
     */
    private static void decodeBody(ByteBuffer in, byte protocolType, FileCommand fileCommand) {
        //解码body
        switch (protocolType) {
            case RequestCode.UPLOAD_REQUEST_START:
                fileCommand.setFileData(FileData.decodeFromBuffer(in, FileDataProtocol.uploadRequestStartFields));
                break;
            case RequestCode.UPLOAD_RESPONSE_START:
                fileCommand.setFileData(FileData.decodeFromBuffer(in, FileDataProtocol.uploadResponseStartFields));
                break;
            case RequestCode.UPLOAD_REQUEST_FILE:
                fileCommand.setFileData(FileData.decodeFromBuffer(in, FileDataProtocol.uploadFileRequestFields));
                break;
            case RequestCode.UPLOAD_RESPONSE_FILE:
                fileCommand.setFileData(FileData.decodeFromBuffer(in, FileDataProtocol.uploadFileResponseFields));
                break;
        }
    }

    /**
     * 不同请求 body部分 编解码字段，静态内部类进行缓存
     */
    private static class FileDataProtocol {
        private static Field fileName;
        private static Field fileSize;
        private static Field fileBuffer;
        private static Field uploadPosition;


        private static Field[] uploadRequestStartFields;
        private static Field[] uploadResponseStartFields;
        private static Field[] uploadFileRequestFields;
        private static Field[] uploadFileResponseFields;

        static {
            try {
                fileName = FileData.class.getDeclaredField("fileName");
                fileSize = FileData.class.getDeclaredField("fileSize");
                fileBuffer = FileData.class.getDeclaredField("fileBuffer");
                uploadPosition = FileData.class.getDeclaredField("uploadPosition");

                uploadFileRequestFields = uploadFileRequestFields();
                uploadRequestStartFields = uploadRequestStartFields();
                uploadResponseStartFields = uploadResponseStartFields();
                uploadFileResponseFields = uploadFileResponseFields();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        private static Field[] uploadFileResponseFields() throws NoSuchFieldException {
            return uploadResponseStartFields();
        }

        private static Field[] uploadFileRequestFields() throws NoSuchFieldException {
            return new Field[]{fileName, fileSize, fileBuffer};
        }

        private static Field[] uploadResponseStartFields() throws NoSuchFieldException {
            return new Field[]{uploadPosition};
        }

        private static Field[] uploadRequestStartFields() throws NoSuchFieldException {
            return new Field[]{fileName, fileSize};
        }
    }


    public byte getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public int getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(int requestSeq) {
        this.requestSeq = requestSeq;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    public int getFileDataLength() {
        return fileDataLength;
    }

    public void setFileDataLength(int fileDataLength) {
        this.fileDataLength = fileDataLength;
    }
}
