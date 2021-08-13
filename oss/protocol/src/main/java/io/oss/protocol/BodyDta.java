package io.oss.protocol;

import com.google.gson.Gson;

/**
 * 私有协议中中{@link Body}的'resp'字段的对象化数据
 *
 * @Author zhicheng
 * @Date 2021/5/24 8:13 下午
 * @Version 1.0
 */
public class BodyDta {

    private Long pullPosition;

    private Integer pullLength;

    private Long fileLength;

    private Long uploadPosition;

    private Long uploadedLength;

    private String errorMsg;

    private String errorCode;


    private String lastModify;

    private String contentType;

    //------------- newBody-----------

    private String filePath;

    private Long fullFileLength;

    //------------- upload----------
    private Long nextPushOffset;

    private Long currentLength;

    private Integer finishUploadAck;


    public BodyDta() {
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getPullPosition() {
        return pullPosition;
    }

    public void setPullPosition(Long pullPosition) {
        this.pullPosition = pullPosition;
    }

    public Integer getPullLength() {
        return pullLength;
    }

    public void setPullLength(Integer pullLength) {
        this.pullLength = pullLength;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public Long getUploadPosition() {
        return uploadPosition;
    }

    public void setUploadPosition(Long uploadPosition) {
        this.uploadPosition = uploadPosition;
    }

    public Long getUploadedLength() {
        return uploadedLength;
    }

    public void setUploadedLength(Long uploadedLength) {
        this.uploadedLength = uploadedLength;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public static BodyDta fromJson(String body) {
        Gson gson = new Gson();
        return gson.fromJson(body, BodyDta.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Long getNextPushOffset() {
        return nextPushOffset;
    }

    public void setNextPushOffset(Long nextPushOffset) {
        this.nextPushOffset = nextPushOffset;
    }

    public Long getFullFileLength() {
        return fullFileLength;
    }

    public void setFullFileLength(Long fullFileLength) {
        this.fullFileLength = fullFileLength;
    }

    public Long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(Long currentLength) {
        this.currentLength = currentLength;
    }

    public Integer getFinishUploadAck() {
        return finishUploadAck;
    }

    public void setFinishUploadAck(Integer finishUploadAck) {
        this.finishUploadAck = finishUploadAck;
    }

    public static class Builder {
        private final BodyDta msgExtension = new BodyDta();

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setLastModify(String lastModify) {
            msgExtension.lastModify = lastModify;
            return this;
        }


        public Builder setFilePath(String filePath) {
            msgExtension.filePath = filePath;
            return this;
        }


        public Builder setPullPosition(Long pullPosition) {
            msgExtension.pullPosition = pullPosition;
            return this;
        }


        public Builder setPullLength(Integer pullLength) {
            msgExtension.pullLength = pullLength;
            return this;
        }


        public Builder setFileLength(Long fileLength) {
            msgExtension.fileLength = fileLength;
            return this;
        }


        public Builder setUploadPosition(Long uploadPosition) {
            msgExtension.uploadPosition = uploadPosition;
            return this;
        }


        public Builder setUploadedLength(Long uploadedLength) {
            msgExtension.uploadedLength = uploadedLength;
            return this;
        }

        public Builder setErrorMsg(String errorMsg) {
            msgExtension.errorMsg = errorMsg;
            return this;
        }

        public Builder setErrorCode(String errorCode) {
            msgExtension.errorCode = errorCode;
            return this;
        }

        public Builder setContentType(String contentType) {
            msgExtension.contentType = contentType;
            return this;
        }

        public Builder setNextPushOffset(Long nextPushOffset) {
            msgExtension.nextPushOffset = nextPushOffset;
            return this;
        }

        public Builder setFullFileLength(Long fullFileLength) {
            msgExtension.fullFileLength = fullFileLength;
            return this;
        }

        public Builder setCurrentLength(Long currentLength) {
            msgExtension.currentLength = currentLength;
            return this;
        }

        public Builder setFinishUploadAck(Integer finishUploadAck) {
            msgExtension.finishUploadAck = finishUploadAck;
            return this;
        }

        public BodyDta build() {
            return msgExtension;
        }

    }


}
