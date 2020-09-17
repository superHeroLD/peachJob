package com.ld.peach.job.core.exception.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @ClassName ExceptionHelper
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/16
 * @Version 1.0
 */
public class ExceptionHelper {

    /**
     * 获取异常信息
     *
     * @param t 异常
     * @return 异常信息
     */
    public static String getErrorInfo(Throwable t) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String str = sw.toString();
            sw.close();
            pw.close();
            return str;
        } catch (Exception ex) {
            return "获得Exception信息发生异常";
        }
    }
}
