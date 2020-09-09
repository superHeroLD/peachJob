package com.ld.peach.job.core.util;

/**
 * @ClassName StringUtils
 * @Description String Utils
 * @Author lidong
 * @Date 2020/9/9
 * @Version 1.0
 */
public class StringUtils {

    private StringUtils() {

    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }
}
