package com.ld.peach.job.core.util.date;

import com.ld.peach.job.core.util.ObjectUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName DateTime
 * @Description Date 包装类
 * @Author lidong
 * @Date 2020/9/11
 * @Version 1.0
 */
public class DateTime extends Date {

    /**
     * 时区
     */
    private TimeZone timeZone;

    public DateTime(Date date) {
        this(date.getTime(), (date instanceof DateTime) ? ((DateTime) date).timeZone : TimeZone.getDefault());
    }

    public DateTime() {
        this(System.currentTimeMillis(), TimeZone.getDefault());
    }

    public DateTime(long timeMillis) {
        this(timeMillis, TimeZone.getDefault());
    }

    public DateTime(Date date, TimeZone timeZone) {
        this(date.getTime(), timeZone);
    }

    public DateTime(long timeMillis, TimeZone timeZone) {
        super(timeMillis);
        this.timeZone = Objects.nonNull(timeZone) ? timeZone : TimeZone.getDefault();
    }

    /**
     * 获得TimeZone
     *
     * @return timeZone
     */
    public TimeZone getTimeZone() {
        return ObjectUtil.defaultIfNull(timeZone, TimeZone.getDefault());
    }

    /**
     * 调整日期和时间<br>
     *
     * @param datePart 调整的部分 {@link DateField}
     * @param offset   偏移量，正数为向后偏移，负数为向前偏移
     * @return 如果此对象为可变对象，返回自身，否则返回新对象
     */
    public DateTime offset(DateField datePart, int offset) {
        if (DateField.ERA == datePart) {
            throw new IllegalArgumentException("ERA is not support offset!");
        }

        final Calendar cal = toCalendar();
        //noinspection MagicConstant
        cal.add(datePart.getValue(), offset);

        return this.setTimeInternal(cal.getTimeInMillis());
    }

    /**
     * 转换为Calendar
     */
    public Calendar toCalendar() {
        return toCalendar(getTimeZone(), Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * 转换为Calendar
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar(TimeZone zone, Locale locale) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final Calendar cal = (null != zone) ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(this);
        return cal;
    }

    private DateTime setTimeInternal(long time) {
        super.setTime(time);
        return this;
    }


    public String format(String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(this);
    }

    @Override
    public String toString() {
        if (null != timeZone) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
            simpleDateFormat.setTimeZone(timeZone);
            return toString(simpleDateFormat);
        }
        return format(DatePattern.NORM_DATETIME_PATTERN);
    }

    public String toString(DateFormat format) {
        return format.format(this);
    }
}
