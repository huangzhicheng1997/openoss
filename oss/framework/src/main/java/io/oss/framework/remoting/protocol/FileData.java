package io.oss.framework.remoting.protocol;

import io.netty.util.internal.StringUtil;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhicheng
 * @date 2021-01-18 14:20
 */
public class FileData {

    /**
     * 文件名
     */
    private String fileName;

    private Integer fileBufferSize;

    /**
     * 文件发送缓冲区
     */
    private ByteBuffer fileBuffer;

    /**
     * 文件大小
     */
    private Long fileSize;

    private Long uploadPosition;

    private Long downloadPosition;

    public Integer getObjectSize() {
        int size = 0;

        if (!StringUtil.isNullOrEmpty(fileName)) {
            //文件名域+文件名长度域
            size = size + fileName.getBytes().length + Integer.BYTES;
        }
        if (null != fileBuffer) {
            //fileBuffer长度域长度+fileBuffer长度
            size = size +Integer.BYTES+ fileBuffer.capacity();
        }

        if (null != fileSize) {
            size = size + Long.BYTES;
        }
        if (null != downloadPosition) {
            size = size + Long.BYTES;
        }
        if (null != uploadPosition) {
            size = size + Long.BYTES;
        }
        return size;
    }

    public ByteBuffer encodeToBuffer() {

        ByteBuffer allocate = ByteBuffer.allocate(getObjectSize());

        if (null != fileSize) {
            allocate.putLong(fileSize);
        }
        if (uploadPosition != null) {
            allocate.putLong(uploadPosition);
        }
        if (downloadPosition != null) {
            allocate.putLong(downloadPosition);
        }

        if (null != fileBuffer) {
            allocate.putInt(fileBuffer.capacity());
            allocate.put(fileBuffer);
        }

        if (!StringUtil.isNullOrEmpty(fileName)) {
            allocate.putInt(fileName.getBytes().length);
            allocate.put(fileName.getBytes());
        }
        allocate.flip();
        return allocate;
    }

    public static FileData decodeFromBuffer(ByteBuffer in, Field[] fields) {
        List<String> decodeFields = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
        FileData fileData = new FileData();
        if (decodeFields.contains("fileSize")) {
            fileData.setFileSize(in.getLong());
        }
        if (decodeFields.contains("uploadPosition")) {
            fileData.setUploadPosition(in.getLong());
        }
        if (decodeFields.contains("downloadPosition")) {
            fileData.setDownloadPosition(in.getLong());
        }
        if (decodeFields.contains("fileBuffer")) {
            int bufferSize = in.getInt();
            fileData.fileBufferSize=bufferSize;
            byte[] bytes = new byte[bufferSize];
            in.get(bytes);
            fileData.setFileBuffer(ByteBuffer.wrap(bytes));
        }
        if (decodeFields.contains("fileName")) {
            int fileNameLength = in.getInt();
            byte[] bytes = new byte[fileNameLength];
            in.get(bytes);
            fileData.setFileName(new String(bytes, StandardCharsets.UTF_8));
        }
        return fileData;

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ByteBuffer getFileBuffer() {
        return fileBuffer;
    }

    public void setFileBuffer(ByteBuffer fileBuffer) {
        this.fileBuffer = fileBuffer;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public long getUploadPosition() {
        return uploadPosition;
    }

    public void setUploadPosition(long uploadPosition) {
        this.uploadPosition = uploadPosition;
    }

    public long getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(long downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public Integer getFileBufferSize() {
        return fileBufferSize;
    }

    public void setFileBufferSize(Integer fileBufferSize) {
        this.fileBufferSize = fileBufferSize;
    }
}
