package io.oss.file.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 针对高频访问的文件，所创建高速缓冲池。缓冲由自己释放内存
 *
 * @Author zhicheng
 * @Date 2021/5/16 5:25 下午
 * @Version 1.0
 */
public class DirectLRUBufferCache {

    private final boolean fastCacheOpen;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Long cacheTotalSize = 1073741824L;

    private final AtomicLong cachedSize = new AtomicLong(0);

    private final Lock lock = new ReentrantLock();

    private final LRU<String, ByteBuffer> lru = new LRU<>();

    public DirectLRUBufferCache(boolean fastCacheOpen) {
        this.fastCacheOpen = fastCacheOpen;
    }


    public DirectLRUBufferCache(Boolean fastCacheOpen, Long cacheTotalSize) {
        this.fastCacheOpen = fastCacheOpen;
        this.cacheTotalSize = cacheTotalSize;
    }


    /**
     * 尝试获取缓冲。客户端代码对buffer的指针修改不会直接反应到缓冲区
     *
     * @param key key
     * @return 获取缓存的缓冲区。如果锁竞争失败或者不存在缓存，则返回null
     */
    public ByteBuffer tryGet(String key) {
        if (lock.tryLock()) {
            try {
                ByteBuffer cache = lru.get(key);
                if (cache != null) {
                    return cache.slice();
                }
            } finally {
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 获取缓冲，一直阻塞到获取到缓存
     *
     * @param key key
     * @return 缓存
     */
    public ByteBuffer get(String key) {
        lock.lock();
        try {
            ByteBuffer cache = lru.get(key);
            if (cache != null) {
                return cache.slice();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 设置缓冲
     *
     * @param key   {@link String }
     * @param value {@link ByteBuffer} 保证为读模式
     */
    public void putCacheIfAbsent(String key, ByteBuffer value) {
        lock.lock();
        try {
            int newMemory = value.capacity();
            cachedSize.addAndGet(newMemory);
            ByteBuffer direct = ByteBuffer.allocateDirect(value.remaining());
            direct.put(value);
            direct.flip();
            lru.putIfAbsent(key, direct);
        } finally {
            lock.unlock();
        }
    }


    private class LRU<K extends String, V extends ByteBuffer> extends LinkedHashMap<K, V> {

        LRU() {
            super(1 << 4, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            boolean needRemove = cachedSize.intValue() > cacheTotalSize;
            if (needRemove) {
                int capacity = eldest.getValue().capacity();
                cachedSize.addAndGet(capacity * -1);
                if (logger.isDebugEnabled()) {
                    logger.debug("lru buffer cache over flow, remove eldest cache, cachedFileName:" + eldest.getKey());
                }
                //释放内存
                DirectBuffer directBuffer = (DirectBuffer) eldest.getValue();
                directBuffer.cleaner().clean();
            }
            return needRemove;
        }

    }


}
