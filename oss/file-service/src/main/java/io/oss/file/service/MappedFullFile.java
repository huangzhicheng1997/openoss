package io.oss.file.service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import io.oss.util.exception.FileNotFindException;
import io.oss.systemcall.SystemCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 安照文件大小可以动态多次映射文件，每次映射的最大值为1G，
 * 对于调用者来说屏蔽了底层逻辑，完全可以认为为一个映射。
 * 支持释放文件描述符和句柄。读取和释放采用读写锁支持高速访问。
 * 小文件一般请求量较大例如图片，文本数据，针对小文件进行页面预调，因为mmap采取完全按需调页，这样可以减少过多的缺页异常。
 */
public class MappedFullFile {

    //1G
    private static final int partitionSize = 1024 * 1024 * 1024;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //一个页面大小 一般为4KB
    private static final Integer PAGE_SIZE;

    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final List<MappedByteBuffer> mappedByteBuffers = new CopyOnWriteArrayList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private volatile boolean isCleaning = false;

    /**
     * 页面预调的文件大小阈值
     */
    private Integer preLoadThreshold = 32 * 1024 * 1024;

    static {
        PAGE_SIZE = SystemCall.INSTANCE.getpagesize();
    }

    public MappedFullFile(String filePath) {
        if (readWriteLock.writeLock().tryLock()) {
            try {
                this.file = new File(filePath);
                if (!file.exists()) {
                    throw new FileNotFindException();
                }
                this.randomAccessFile = new RandomAccessFile(file, "rw");
                map0(file.length() - 1);
                //小文件进行全页面预调
                if (file.length() <= preLoadThreshold) {
                    mappedByteBuffers.forEach(MappedByteBuffer::load);
                } else {
                    //大文件处理
                    mappedByteBuffers.forEach(mappedByteBuffer -> {
                        long address = ((DirectBuffer) mappedByteBuffer).address();
                        Pointer pointer = new Pointer(address);
                        //向内核提交页面调用优化建议
                        SystemCall.INSTANCE.madvise(pointer, new NativeLong(mappedByteBuffer.capacity()), SystemCall.MADV_WILLNEED);
                    });
                    logger.debug("big file handle");
                }

            } catch (FileNotFoundException ex) {
                throw new FileNotFindException(ex.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } else {
            throw new ConcurrentCreateMappedFileException("file already mapped or mapping , path:" + filePath);
        }
    }

    private void map0(long end) throws IOException {
        assert (long) 0 < end;
        //get partition number (0-end)
        int beginBlockNum = (int) (long) 0 / partitionSize;
        int endBlockNum = (int) end / partitionSize;
        /* int fileEndBlockNum = (int) (file.length() - 1) / partitionSize;*/

        long beginPosition = beginBlockNum * partitionSize;

        //初始块和末尾块为同一块，则只需要映射一次
        if (beginBlockNum == endBlockNum) {
            mappedByteBuffers
                    .add(randomAccessFile
                            .getChannel()
                            .map(FileChannel.MapMode.PRIVATE,
                                    beginPosition, end + 1
                            ));
            return;
        }

        int needToMapBlockTimes = endBlockNum - beginBlockNum + 1;
        for (int i = 0; i < needToMapBlockTimes; i++) {
            //获取映射的位置
            int blockIndex = beginBlockNum + i;
            //当前块是否已经映射
            if (mappedByteBuffers.size() > blockIndex && null != mappedByteBuffers.get(blockIndex)) {
                continue;
            }

            //尾部映射特别处理
            if (i + beginBlockNum == endBlockNum) {
                mappedByteBuffers
                        .add(randomAccessFile
                                .getChannel()
                                .map(FileChannel.MapMode.PRIVATE,
                                        beginPosition + i * partitionSize,
                                        end - (beginPosition + i * partitionSize) + 1));
                continue;
            }


            mappedByteBuffers
                    .add(randomAccessFile
                            .getChannel()
                            .map(FileChannel.MapMode.PRIVATE,
                                    beginPosition + i * partitionSize,
                                    partitionSize));

        }
    }


    /**
     * @param begin  开始
     * @param length 拉取的长度
     * @return null拉取被中断，不为null拉取成功
     */
    public ByteBuffer pullPartOfFile(long begin, int length) {
        if (isCleaning) {
            return null;
        }
        readWriteLock.readLock().lock();
        try {
            if (!isCleaning) {
                assert length < Integer.MAX_VALUE;
                int end = length - 1;
                Integer beginPart = getPart(begin);
                int endPart = getPart(end);
                byte[] fileBuffer = new byte[length];
                //处于相同的一块缓冲区
                if (beginPart.equals(endPart)) {
                    int partBegin = (int) (begin % partitionSize);
                    MappedByteBuffer mappedByteBuffer = mappedByteBuffers.get(beginPart);
                    ByteBuffer slice = mappedByteBuffer.slice();
                    slice.position(partBegin);
                    slice.get(fileBuffer);
                } else {
                    //遍历目标缓冲块
                    for (int i = beginPart, nextOffset = 0; i <= endPart; i++) {
                        MappedByteBuffer mappedByteBuffer = mappedByteBuffers.get(i);
                        ByteBuffer slice = mappedByteBuffer.slice();
                        //读取起始缓冲块
                        if (i == beginPart) {
                            int partBegin = beginPart % partitionSize;
                            slice.position(partBegin);
                            int remaining = slice.remaining();
                            slice.get(fileBuffer, 0, remaining);
                            nextOffset = nextOffset + remaining;
                            continue;
                        }
                        //读取末尾缓冲块
                        if (i == endPart) {
                            int partEndPosition = (int) (end % partitionSize);
                            slice.get(fileBuffer, nextOffset, partEndPosition + 1);
                            continue;
                        }
                        //读取中间缓冲块
                        slice.get(fileBuffer, nextOffset, partitionSize);
                        nextOffset = nextOffset + partitionSize;
                    }
                }

                return ByteBuffer.wrap(fileBuffer);
            }
            return null;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private Integer getPart(long begin) {
        return Math.toIntExact(begin / partitionSize);
    }

    /**
     * 释放文件资源
     */
    public void cleanup() {
        if (isCleaning) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            if (!isCleaning) {
                isCleaning = true;
                mappedByteBuffers.forEach(this::unMap);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void unMap(MappedByteBuffer mappedByteBuffer) {
        try {
            Method method = mappedByteBuffer.getClass()
                    .getDeclaredMethod("cleaner");
            method.setAccessible(true);
            Cleaner cleaner = (Cleaner) method.invoke(mappedByteBuffer);
            cleaner.clean();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Integer getPreLoadThreshold() {
        return preLoadThreshold;
    }

    public void setPreLoadThreshold(Integer preLoadThreshold) {
        this.preLoadThreshold = preLoadThreshold;
    }

    public String getFilePath() {
        return file.getPath();
    }

    public long getFileSize() {
        return file.length();
    }
}
