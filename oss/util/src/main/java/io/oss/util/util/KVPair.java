package io.oss.util.util;

/**
 * @Author zhicheng
 * @Date 2021/4/15 7:40 下午
 * @Version 1.0
 */
public class KVPair<K, V> {
    private K key;
    private V v;

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

    public void setV(V v) {
        this.v = v;
    }
}
