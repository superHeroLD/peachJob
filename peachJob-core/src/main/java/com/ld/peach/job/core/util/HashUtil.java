package com.ld.peach.job.core.util;

/**
 * @ClassName HashUtil
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
public class HashUtil {

    /**
     * 取得Hash码
     * 直接照搬hashMap的实现
     *
     * @param key key
     * @return hash code
     */
    public static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
