package io.oss.file.service;

/**
 * @author zhicheng
 * @date 2021-07-17 17:20
 */
public class FileMetaData {
    /**
     * ⽂件⻓度
     */
    private long fileLength;
    /**
     * 完整文件的长度
     */
    private long fullFileLength;
    /**
     * 更新时间
     */
    private long lastModifyTime;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 是否被删除 1删除 0未删除
     */
    private int isDeleted;
    /**
     * 是否为⽬录 1⽬录，2⽂件
     */
    private int isDir;
    /**
     * 文件名
     */
    private String fileName;

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getFullFileLength() {
        return fullFileLength;
    }

    public void setFullFileLength(long fullFileLength) {
        this.fullFileLength = fullFileLength;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getIsDir() {
        return isDir;
    }

    public void setIsDir(int isDir) {
        this.isDir = isDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
