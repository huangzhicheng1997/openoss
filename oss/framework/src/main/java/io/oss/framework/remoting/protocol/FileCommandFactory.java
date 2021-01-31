package io.oss.framework.remoting.protocol;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产多个请求命令的工厂，
 *
 * @author zhicheng
 * @date 2021-01-22 9:58
 */
public class FileCommandFactory {

    private static final AtomicInteger idGen = new AtomicInteger(0);

    protected static int getId() {
        return idGen.incrementAndGet() % Integer.MAX_VALUE;
    }

    public static class RequestFactory {
        /**
         * 上传握手请求
         *
         * @return
         */
        public static FileCommand uploadStartRequest(String fileName, Long fileSize) {
            FileCommand fileCommand = new FileCommand();
            //设置header
            fileCommand.setProtocolType(RequestCode.UPLOAD_REQUEST_START);
            fileCommand.setRequestSeq(FileCommandFactory.getId());

            //设置body
            FileData fileData = new FileData();
            fileData.setFileName(fileName);
            fileData.setFileSize(fileSize);

            fileCommand.setFileDataLength(fileData.getObjectSize());
            fileCommand.setFileData(fileData);
            return fileCommand;
        }

        /**
         * 上传文件
         *
         * @param fileName
         * @param fileSize
         * @param fileBuffer
         * @return
         */
        public static FileCommand uploadFileRequest(String fileName, Long fileSize, ByteBuffer fileBuffer) {
            FileCommand fileCommand = new FileCommand();
            fileCommand.setProtocolType(RequestCode.UPLOAD_REQUEST_FILE);
            fileCommand.setRequestSeq(getId());

            FileData fileData = new FileData();
            fileData.setFileName(fileName);
            fileData.setFileSize(fileSize);
            fileData.setFileBufferSize(fileBuffer.capacity());
            fileData.setFileBuffer(fileBuffer);
            fileCommand.setFileDataLength(fileData.getObjectSize());
            fileCommand.setFileData(fileData);
            return fileCommand;
        }
    }

    public static class ResponseFactory {
        /**
         * 上传握手响应
         *
         * @param requestSeq
         * @return
         */
        public static FileCommand uploadStartResponse(int requestSeq, long eofPosition) {

            FileCommand fileCommand = new FileCommand();
            fileCommand.setRequestSeq(requestSeq);
            fileCommand.setProtocolType(RequestCode.UPLOAD_RESPONSE_START);

            //返回已上传的文件末尾偏移量
            FileData fileData = new FileData();
            fileData.setUploadPosition(eofPosition);

            fileCommand.setFileDataLength(fileData.getObjectSize());
            fileCommand.setFileData(fileData);

            return fileCommand;

        }

        /**
         * 复位报文
         */
        public static FileCommand uploadResetResponse(int requestSeq, long eofPosition) {
            FileCommand fileCommand = new FileCommand();
            fileCommand.setRequestSeq(requestSeq);
            fileCommand.setProtocolType(RequestCode.UPLOAD_RESET);
            //返回已上传的文件末尾偏移量
            FileData fileData = new FileData();
            fileData.setUploadPosition(eofPosition);

            fileCommand.setFileDataLength(fileData.getObjectSize());
            fileCommand.setFileData(fileData);
            return fileCommand;
        }

        public static FileCommand uploadFileResponse(int requestSeq, long eofPosition) {

            FileCommand fileCommand = new FileCommand();
            fileCommand.setRequestSeq(requestSeq);
            fileCommand.setProtocolType(RequestCode.UPLOAD_RESPONSE_FILE);

            //返回已上传的文件末尾偏移量
            FileData fileData = new FileData();
            fileData.setUploadPosition(eofPosition);

            fileCommand.setFileDataLength(fileData.getObjectSize());
            fileCommand.setFileData(fileData);

            return fileCommand;

        }
    }
}
