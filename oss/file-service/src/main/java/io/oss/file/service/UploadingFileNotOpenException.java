package io.oss.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author zhicheng
 * @date 2021-05-20 15:31
 */
public class UploadingFileNotOpenException extends RuntimeException {
    public UploadingFileNotOpenException() {
    }

    public UploadingFileNotOpenException(String message) {
        super(message);
    }

    public UploadingFileNotOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadingFileNotOpenException(Throwable cause) {
        super(cause);
    }

    public UploadingFileNotOpenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
