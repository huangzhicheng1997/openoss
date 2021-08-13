package io.oss.file.service;


import io.oss.util.util.KVPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhicheng
 * @date 2021-05-18 17:01
 */
public class PushFileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, KVPair<UploadingFile, Long>> uploadingFileMap = new ConcurrentHashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Long uploadingFileFreeTime = 3000L;

    /**
     * 获取已上传文件的位置
     *
     * @param filePath 路径
     * @return 位置
     */
    public Long getUploadOffset(String filePath) throws IOException {
        readWriteLock.readLock().lock();
        try {
            return getUploadingFile(filePath).length() - 1;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 上传字节
     *
     * @param filePath   路径
     * @param byteBuffer 缓存区
     * @param position   位置
     */
    public void pushBuffer(String filePath, ByteBuffer byteBuffer, long position) throws IOException {
        readWriteLock.readLock().lock();
        try {
            UploadingFile uploadingFile = getUploadingFile(filePath);
            uploadingFile.pushPartOfFileSync(byteBuffer, position);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 上传完成结束上传
     *
     * @param filePath 文件路径
     */
    public void finish(String filePath) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            UploadingFile uploadingFile = getUploadingFile(filePath);
            uploadingFile.finishUpload();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 交由后台线程，去不断清理过期资源
     */
    public void daemonClear() {
        readWriteLock.writeLock().lock();
        try {
            uploadingFileMap.forEach((filePath, uploadingKVPair) -> {
                long freeTime = System.currentTimeMillis() - uploadingKVPair.getV();
                if (freeTime >= uploadingFileFreeTime) {
                    uploadingFileMap.remove(filePath);
                    UploadingFile uploadingFile = uploadingKVPair.getKey();
                    try {
                        uploadingFile.close();
                    } catch (IOException e) {
                        logger.warn("clear uploadFile exception filePath:" + filePath, e);
                    }
                }

            });
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private UploadingFile getUploadingFile(String filePath) throws IOException {
        UploadingFile uploadingFile;
        KVPair<UploadingFile, Long> pair = uploadingFileMap.get(filePath);
        if (null == pair) {
            uploadingFile = new UploadingFile();
            uploadingFileMap.putIfAbsent(filePath, new KVPair<>(uploadingFile, System.currentTimeMillis()));
        } else {
            uploadingFile = pair.getKey();
            //更新最近使用时间
            boolean isOk;
            do {
                isOk = pair.compareAndSetV(System.currentTimeMillis(), pair.getV());
            } while (!isOk);

        }
        uploadingFile.open(filePath);

        return uploadingFile;
    }

    public Long getUploadingFileFreeTime() {
        return uploadingFileFreeTime;
    }

    public void setUploadingFileFreeTime(Long uploadingFileFreeTime) {
        this.uploadingFileFreeTime = uploadingFileFreeTime;
    }

}
