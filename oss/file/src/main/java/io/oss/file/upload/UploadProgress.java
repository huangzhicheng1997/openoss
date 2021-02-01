package io.oss.file.upload;

import io.oss.util.exception.UploadException;

import java.math.BigDecimal;

public class UploadProgress {

    private Long alreadyUpload = 0L;

    private final Long fileSize;

    private Long startTime = System.currentTimeMillis();

    private Long completeTime = 0L;


    private Boolean isComplete = false;

    public UploadProgress(Long fileSize) {
        this.fileSize = fileSize;
    }

    public synchronized void finish() {
        isComplete = true;
        completeTime = System.currentTimeMillis();
    }

    public synchronized Long getUseTime() {
        if (!isComplete) {
            throw new UploadException("upload task is not finished yet!");
        }
        return completeTime - startTime;
    }

    public synchronized void uploadedRecord(Long alreadyUpload) {
        this.alreadyUpload = alreadyUpload;
    }

    public synchronized String getProgress() {
        BigDecimal progress = new BigDecimal((double) alreadyUpload / fileSize);
        return progress.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

}
