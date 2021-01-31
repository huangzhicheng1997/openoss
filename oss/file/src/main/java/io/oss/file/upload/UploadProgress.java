package io.oss.file.upload;

import io.oss.util.exception.UploadException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UploadProgress {

    public Long alreadyUpload = 0L;

    public final Long fileSize;

    public Long timeToCalculate = System.currentTimeMillis();

    public Long hasSend = 0L;

    public Long startTime = System.currentTimeMillis();

    public Long completeTime = 0L;

    public Long lastSpeed = 0L;

    public Boolean isComplete = false;

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

    public synchronized void uploadedRecord(Integer alreadyUpload) {
        this.alreadyUpload = this.alreadyUpload + alreadyUpload;
        this.hasSend = this.hasSend + alreadyUpload;
    }

    public synchronized String getProgress() {
        BigDecimal progress = new BigDecimal((double) alreadyUpload / fileSize);
        return progress.setScale(2, RoundingMode.UP).toString();
    }

    public synchronized Long getSpeed() {
        if ((System.currentTimeMillis() - timeToCalculate) >= 1000) {
            long speed = hasSend / ((System.currentTimeMillis() - timeToCalculate) / 1000);
            hasSend = 0L;
            timeToCalculate = System.currentTimeMillis();
            return speed;
        }
        return lastSpeed;
    }

}
