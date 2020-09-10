package com.ld.peach.job.core.util;

import java.util.Objects;

/**
 * @ClassName CheckUtils
 * @Description CheckUtils
 * @Author lidong
 * @Date 2020/9/10
 * @Version 1.0
 */
public class CheckUtil {


    public static void checkState(boolean expression, Object errorMessage) {
        if (Objects.isNull(errorMessage)) {
            throw new IllegalStateException("errorMessage can't be null");
        }

        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
}
