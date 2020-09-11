package com.ld.peach.job.core.util.date;

import com.ld.peach.job.core.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName DateUtil
 * @Description 日期工具类
 * @Author lidong
 * @Date 2020/9/10
 * @Version 1.0
 */
public class DateUtil {

    /**
     * 根据特定格式格式化日期
     * <p>
     * TODO 要加时区
     *
     * @param date   被格式化的日期
     * @param format 日期格式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format) {
        if (null == date || StringUtil.isBlank(format)) {
            return null;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        return format(date, sdf);
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DateFormat format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }


    /**
     * 计算日期偏移量，秒级别
     *
     * @param date   给定日期
     * @param offset 偏移量
     * @return DateTime date 包装类，可以直接映射到Date
     */
    public static DateTime offsetSecond(Date date, int offset) {
        return offset(date, DateField.SECOND, offset);
    }

    /**
     * 计算日期偏移量，分钟级别
     *
     * @param date   给定日期
     * @param offset 偏移量
     * @return DateTime date 包装类，可以直接映射到Date
     */
    public static DateTime offsetMinute(Date date, int offset) {
        return offset(date, DateField.MINUTE, offset);
    }

    /**
     * 计算日期偏移量，小时级别
     *
     * @param date   给定日期
     * @param offset 偏移量
     * @return DateTime date 包装类，可以直接映射到Date
     */
    public static DateTime offsetHour(Date date, int offset) {
        return offset(date, DateField.HOUR, offset);
    }

    /**
     * 计算日期偏移量，天级别
     *
     * @param date   给定日期
     * @param offset 偏移量
     * @return DateTime date 包装类，可以直接映射到Date
     */
    public static DateTime offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }

    /**
     * 计算日期偏移量
     * 不影响原来的日期
     *
     * @param date      给定日期
     * @param dateField 偏移时间单位
     * @param offset    偏移量
     * @return DateTime
     */
    public static DateTime offset(Date date, DateField dateField, int offset) {
        return new DateTime(date).offset(dateField, offset);
    }
}
