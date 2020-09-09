package com.ld.peach.job.core.util;

import java.util.Map;

/**
 * @ClassName MapUtils
 * @Description Map Utils
 * @Author lidong
 * @Date 2020/9/9
 * @Version 1.0
 */
public class MapUtils {

    private MapUtils() {

    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !MapUtils.isEmpty(map);
    }
}
