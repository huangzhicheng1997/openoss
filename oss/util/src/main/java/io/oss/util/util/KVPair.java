package io.oss.util.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @Author zhicheng
 * @Date 2021/4/15 7:40 下午
 * @Version 1.0
 */
public class KVPair<K, V> {
    private K key;
    private V v;

    private static final Unsafe unSafe;
    private static final long valueFieldOffset;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unSafe = (Unsafe) theUnsafe.get(null);
            valueFieldOffset = unSafe.objectFieldOffset(KVPair.class.getDeclaredField("v"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KVPair(K key, V v) {
        this.key = key;
        this.v = v;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getV() {
        return v;
    }

    public boolean compareAndSetV(V newValue, V oldValue) {
        return unSafe.compareAndSwapObject(this, valueFieldOffset, oldValue, newValue);
    }

    public void setV(V v) {
        this.v = v;
    }

}
