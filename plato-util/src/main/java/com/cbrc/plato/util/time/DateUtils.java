/**
 * Author:   Xian
 * FileName: DateUtils
 * Date:     2019/2/21 15:57
 */
package com.cbrc.plato.util.time;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class DateUtils {
    final static public String FULL_ST_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final static public String FULL_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final static public String FULL_J_FORMAT = "yyyy/MM/dd HH:mm:ss";
    final static public String CURRENCY_ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String CURRENCY_J_FORMAT = "yyyy/MM/dd HH:mm";
    final static public String DATA_FORMAT = "yyyyMMddHHmmss";
    final static public String ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String ST_CN_FORMAT = "yyyy年MM月dd日 HH:mm";
    final static public String CN_FORMAT = "yy年MM月dd日HH:mm";
    final static public String DAY_FORMAT = "yyyy-MM-dd";
    final static public String SHORT_DATE_FORMAT = "yy-MM-dd";
    final static public String YEAR_FORMAT = "yyyy";
    final static public String MOUTH_DATE_TIME = "MM-dd HH:mm:ss";
    final static public String MOUTH_DATE = "MM 月dd 日";
    final static public String MOUTH_FORMAT = "yyyy-MM";
    final static public String VAL_TIMESTAMP_FORMAT = "yyyy-MM-dd-HH.mm.ss.SSS";
    final static public String HH_MM_FORMAT = "HH:mm";

    public static Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static java.sql.Date currentSqlDate() {
        return new java.sql.Date(new Date().getTime());
    }

    public static String format() {
        return format(FULL_ST_FORMAT);
    }

    public static String format(String pattern) {
        return format(new Date(), pattern);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static Timestamp formatTimestamp(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(dateStr);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
        }
        return null;
    }

    public static Date formatDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(dateStr);
            return date;
        } catch (ParseException e) {}
        return null;
    }

    public static java.sql.Date formatSqlDate(String dateStr, String pattern) {
        Date date = formatDate(dateStr, pattern);
        if (null != date) {
            return new java.sql.Date(date.getTime());
        }
        return null;
    }

    /**
     * @param itmp 偏移量
     * @return Date 得到变化号数的日期
     */
    public static Date getDate(int itmp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, itmp);
        return calendar.getTime();
    }

    /**
     * @param itmp 偏移量
     * @return Date 得到变化号数的日期
     */
    public static Date getDate(int itmp, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, itmp);
        return calendar.getTime();
    }

    /**
     * 比较较两个日期,返回天数差
     * @param beginDate 开始日期时间
     * @param endDate 结束日期时间
     */
    public static long compareDay(Date beginDate, Date endDate) {
        Calendar endDateYears = new GregorianCalendar();
        endDateYears.setTime(endDate);
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDateYears.getTimeInMillis()
                - beginYears.getTimeInMillis();
        return diffMillis / (24 * 60 * 60 * 1000);
    }

    public static Timestamp getTimestamp(Long time) {
        return new Timestamp(time);

    }


    /**
     * 根据自定格式获取当前日期:pattern:YYYYMMDD
     * @param pattern 时间的格式：YYYYMMDD或yyyyMMddkkmmssSSS等
     */
    public static String getDateTime(String pattern) {
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DAY_OF_MONTH, 0);
        String dt = sdf.format(rightNow.getTime());
        return dt;
    }
}
