package io.oss.file.service;


import io.oss.util.exception.PullMsgTooLongException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhicheng
 * @date 2021-05-17 10:19
 */
public class PullFileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
     * 缓冲区，主要为了减少mmp页面置换以及读时脏页回写的频率
     */
    private final DirectLRUBufferCache directLruBufferCache;

    private final Map<String, MappedFullFileWrapper> mappedFiles = new ConcurrentHashMap<>();

    // 默认映射文件最大空闲时间为 3秒
    private Long mappedFileMaxFreeTime = 3000L;

    //默认可以进入bufferCache的文件大小容量最大值
    private Long perBufferCacheLimitCapacity = 16 * 1024 * 1024L;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 默认一分钟访问频率超过100次，则进入bufferCache
     */
    private Integer addCacheThreshold = 100;

    private final Map<String, AtomicInteger>
            fileAccessFrequencyCounter = new ConcurrentHashMap<>();

    public PullFileService(DirectLRUBufferCache directLruBufferCache) {
        this.directLruBufferCache = directLruBufferCache;
    }


    /**
     * 获取文件长度
     *
     * @param filePath 文件路径
     * @return
     */
    public long getFileLength(String filePath) {
        this.readWriteLock.readLock().lock();
        try {
            mappedFiles.putIfAbsent(filePath,
                    new MappedFullFileWrapper(new MappedFullFile(filePath), System.currentTimeMillis()));
            return mappedFiles.get(filePath).getFileSize();
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }


    /**
     * 拉取指定文件 字节流，调用者建议对length作限制
     *
     * @param filePath 文件路径
     * @param position 偏移
     * @param length   长度
     * @return buffer
     */
    public ByteBuffer pullPartOfFile(String filePath, long position, int length) {
        if (length >= 1024 * 1024 * 30) {
            throw new PullMsgTooLongException("max msg length 30m");
        }
        ByteBuffer returnBuffer;
        ByteBuffer buffer = directLruBufferCache.tryGet(filePath);
        //是否命中缓存
        if (null == buffer) {
            readWriteLock.readLock().lock();
            try {
                for (; ; ) {
                    if (mappedFiles.containsKey(filePath)) {
                        MappedFullFileWrapper mappedFullFileWrapper = mappedFiles.get(filePath);
                        //超出长度校验
                        if (mappedFullFileWrapper.getFileSize() < position + length) {
                            length = (int) (mappedFullFileWrapper.getFileSize() - position);
                        }
                        returnBuffer = mappedFullFileWrapper.pullPartOfFile(position, length);
                        //更新最近使用的时间点
                        mappedFullFileWrapper.setLastUsedTime(System.currentTimeMillis());
                        fileAccessFrequencyIncrement(filePath);
                        break;
                    } else {
                        mappedFiles.putIfAbsent(filePath,
                                new MappedFullFileWrapper(new MappedFullFile(filePath), System.currentTimeMillis()));
                    }
                }
            } finally {
                readWriteLock.readLock().unlock();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("bufferCache hit！");
            }
            byte[] remaining = new byte[buffer.remaining()];
            if (remaining.length < position + length) {
                length = (int) (remaining.length - position);
            }
            buffer.get(remaining, (int) position/*bufferCache内文件必为小文件，可以转int*/, length);
            returnBuffer = ByteBuffer.wrap(remaining);
        }
        return returnBuffer;
    }


    private void fileAccessFrequencyIncrement(String filePath) {
        if (fileAccessFrequencyCounter.containsKey(filePath)) {
            AtomicInteger counter = fileAccessFrequencyCounter.get(filePath);
            counter.incrementAndGet();
        } else {
            AtomicInteger atomicInteger = fileAccessFrequencyCounter.putIfAbsent(filePath, new AtomicInteger(1));
            if (null != atomicInteger) {
                atomicInteger.incrementAndGet();
            }
        }
    }


    /**
     * 释放文件资源，造成短时间停顿
     */
    public void cleanResource() {
        readWriteLock.writeLock().lock();
        try {
            List<MappedFullFileWrapper> needToClean = new ArrayList<>();
            mappedFiles.forEach((k, v) -> {
                Long lastUsedTime = v.lastUsedTime;
                if (System.currentTimeMillis() - lastUsedTime >= mappedFileMaxFreeTime) {
                    needToClean.add(v);
                    if (logger.isDebugEnabled()) {
                        logger.debug("MappedFile cleaning target:" + v.getFilePath());
                    }
                }
            });

            needToClean.forEach(mappedFullFileWrapper -> {
                mappedFiles.remove(mappedFullFileWrapper.getFilePath());
                mappedFullFileWrapper.cleanup();
            });

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    /**
     * 根据计数进行bufferCache的填充
     */
    public void smartAddBufferCache() {
        fileAccessFrequencyCounter.forEach((k, v) -> {
            int accessFrequency = v.intValue();
            if (accessFrequency >= addCacheThreshold) {
                //获取文件容量
                File file = new File(k);
                if (!file.exists()) {
                    return;
                }
                long length = file.length();
                if (length <= this.perBufferCacheLimitCapacity) {
                    ByteBuffer buffer = this.pullPartOfFile(k, 0, (int) length);
                    directLruBufferCache.putCacheIfAbsent(k, buffer);
                    if (logger.isDebugEnabled()) {
                        logger.debug("buffer cache was loaded！fileName:" + k);
                    }
                }
            }
        });
        //清除计数
        fileAccessFrequencyCounter.clear();
    }


    public void addBufferCache(String filePath, ByteBuffer buffer) {
        directLruBufferCache.putCacheIfAbsent(filePath, buffer);
    }

    public Long getMappedFileMaxFreeTime() {
        return mappedFileMaxFreeTime;
    }

    public void setMappedFileMaxFreeTime(Long mappedFileMaxFreeTime) {
        this.mappedFileMaxFreeTime = mappedFileMaxFreeTime;
    }

    public Integer getAddCacheThreshold() {
        return addCacheThreshold;
    }

    public void setAddCacheThreshold(Integer addCacheThreshold) {
        this.addCacheThreshold = addCacheThreshold;
    }

    public void setPerBufferCacheLimitCapacity(Long perBufferCacheLimitCapacity) {
        this.perBufferCacheLimitCapacity = perBufferCacheLimitCapacity;
    }


    static class MappedFullFileWrapper {

        private MappedFullFile mappedFullFile;

        private Long lastUsedTime;

        MappedFullFileWrapper(MappedFullFile mappedFullFile, Long lastUsedTime) {
            this.mappedFullFile = mappedFullFile;
            this.lastUsedTime = lastUsedTime;
        }

        ByteBuffer pullPartOfFile(long begin, int length) {
            return mappedFullFile.pullPartOfFile(begin, length);
        }

        void cleanup() {
            mappedFullFile.cleanup();
        }

        String getFilePath() {
            return mappedFullFile.getFilePath();
        }

        long getFileSize() {
            return mappedFullFile.getFileSize();
        }

        MappedFullFile getMappedFullFile() {
            return mappedFullFile;
        }

        void setMappedFullFile(MappedFullFile mappedFullFile) {
            this.mappedFullFile = mappedFullFile;
        }

        Long getLastUsedTime() {
            return lastUsedTime;
        }

        void setLastUsedTime(Long lastUsedTime) {
            this.lastUsedTime = lastUsedTime;
        }
    }
}
