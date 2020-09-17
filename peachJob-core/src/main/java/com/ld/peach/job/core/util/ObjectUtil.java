package com.ld.peach.job.core.util;

/**
 * @ClassName ObjectUtil
 * @Description Utils for operator Object
 * @Author lidong
 * @Date 2020/9/11
 * @Version 1.0
 */
public class ObjectUtil {

    /**
     * 取目标值，为空则设置默认值
     *
     * @param object       目标
     * @param defaultValue 默认值
     * @param <T>          目标类型
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return (null != object) ? object : defaultValue;
    }
}
