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
import java.util.*;

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
        if (StringUtils.isEmpty(pattern)) {
            pattern = DAY_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DAY_OF_MONTH, 0);
        String dt = sdf.format(rightNow.getTime());
        return dt;
    }

    /**
     *获取当前月份前一个月为YYYYMM格式
     */
    public static String getData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);    //月份减一，得到前一个月，
        return format.format(c.getTime());
    }
    /**
     * 格式化月份
     */
    public static String fillZero(int i){
        String month = "";
        if(i<10){
            month = "0" + i;
        }else{
            month = String.valueOf(i);
        }
        return month;
    }

    /*获取starttime 至 endtime时间段内的时间*/
    public static List<String> getTimeList(String startTime, String endTime, int length){
        if(length == 0){
            return  null;
        }

        List<String> timeList = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMM");

        if(StringUtils.isEmpty(startTime)&&StringUtils.isEmpty(endTime)){   //起止日期都为空，取当期日期
            now.add(Calendar.MONTH, -length);
            for(int i=0;i<length;i++){
                now.add(Calendar.MONTH, 1);    //月份加一，得到后一个月，
                timeList.add(simpleDateFormat.format(now.getTime()));
            }
        }else if(StringUtils.isEmpty(startTime)){       //起始日期为空，返回前length期
            try {
                Date endDate=simpleDateFormat.parse(endTime);
                Calendar dd = Calendar.getInstance();//定义日期实例
                dd.setTime(endDate);//设置日期
                dd.add(Calendar.MONTH, -length);
                for(int i=0;i<length;i++){
                    dd.add(Calendar.MONTH, 1);    //月份加一，得到后一个月，
                    timeList.add(simpleDateFormat.format(dd.getTime()));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }else if(StringUtils.isEmpty(endTime)){         //结束日期为空，返回后length期
            try {
                Date startDate=simpleDateFormat.parse(startTime);
                if(startDate.getTime()>now.getTime().getTime())
                    return null;

                Calendar dd = Calendar.getInstance();//定义日期实例
                dd.setTime(startDate);//设置日期
                timeList.add(simpleDateFormat.format(dd.getTime()));
                for(int i=0;i<length;i++){
                    dd.add(Calendar.MONTH, 1);    //月份加一，得到后一个月，
                    if(dd.getTime().getTime()>now.getTime().getTime()){
                        break;
                    }
                    timeList.add(simpleDateFormat.format(dd.getTime()));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Date startDate=simpleDateFormat.parse(startTime);
                Date endDate=simpleDateFormat.parse(endTime);
                if(startDate.getTime()>endDate.getTime()){
                    log.info(startDate+"大于"+endDate);
                    return null;
                }

                Calendar dd = Calendar.getInstance();//定义日期实例
                dd.setTime(startDate);//设置日期起始时间
                while (dd.getTime().before(endDate)) {//判断是否到结束日期
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                    String str = sdf.format(dd.getTime());
                    log.info(str);//输出日期结果
                    timeList.add(str);
                    dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
                }
                timeList.add(endTime);
                log.info(startDate.toString()+endDate);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return timeList;
    }

    public static List<String> getTimeList(String startTime, String endTime){
        List<String> timeList = new ArrayList<>();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMM");

        if(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)){   //起止日期都为空，取当期日期
            return null;
        }
        if(startTime.equals(endTime)){
            timeList.add(startTime);
            return timeList;
        }

        try {
            Date startDate=simpleDateFormat.parse(startTime);
            Date endDate=simpleDateFormat.parse(endTime);
            if(startDate.getTime()>endDate.getTime()){
                log.info(startDate+"大于"+endDate);
                return null;
            }
            Calendar dd = Calendar.getInstance();//定义日期实例
            dd.setTime(startDate);//设置日期起始时间
            while (dd.getTime().before(endDate)) {//判断是否到结束日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                String str = sdf.format(dd.getTime());
//                System.out.println(str);//输出日期结果
                timeList.add(str);
                dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
            }
            timeList.add(endTime);
//            System.out.println(startDate.toString()+endDate);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return timeList;
    }

    //最近length个月
    public static List<String> getRecentMouth(String endTime,int length){
        return getTimeList(null,endTime,length);
    }

    //最近length个季度
    public static Map<String,String> getRecentQuarter(String endTime,int length) throws Exception{
        Map map = new LinkedHashMap();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMM");
        Date endDate=simpleDateFormat.parse(endTime);

        Calendar dd = Calendar.getInstance();//定义日期实例

        dd.setTime(endDate);//设置日期
        int currentYear = dd.get(Calendar.YEAR);

        int session = getSeason(endDate);
        int month = dd.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
                currentYear --;
                break;
        }
        Date yearFirst = getYearFirst(currentYear);
        dd.setTime(yearFirst);
        dd.add(Calendar.MONTH, session*3-1);

        for(int i = 0; i< length; i++){
            map.put(dd.get(Calendar.YEAR)+"年第"+session+"季度",simpleDateFormat.format(dd.getTime()));
            dd.add(Calendar.MONTH, -3);
            session = session==1?4:session-1;
        }
        return map;
    }

    public static int getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
                season = 4;
                break;
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:
                season = 1;
                break;
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:
                season = 2;
                break;
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
                season = 3;
                break;
            default:
                break;
        }
        return season;
    }

    /**
     * 获取上年末
     * @param year 年份
     * @return Date
     */
    public static Date getLastYearEnd(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年第一天日期,年初
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }


    /*上年同期，计算同比*/
    public static String getYearOnYear(String strDate){
        Date date = formatDate(strDate,"yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -12);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(calendar.getTime());
    }


    /*上月，计算环比*/
    public static String getLastMonth(String strDate){
        Date date = formatDate(strDate,"yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(calendar.getTime());
    }

    /*上季度，计算环比*/
    public static String getLastQuarter(String strDate){
        Map<String,String> quter = null;
        try {
            quter = getRecentQuarter(strDate,2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String  lastSeason = null;
        for (Map.Entry<String, String> entry : quter.entrySet()) {
            lastSeason = entry.getValue();
        }
        return lastSeason;
    }

    /*年初*/
    public static String getYearFirstMonth(String strDate){
        Date date = formatDate(strDate,"yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        Date yearFirst = getYearFirst(year);
        return format(yearFirst,"yyyyMM");
    }

    /**
     * 获取上年末  计算年初
     * @param strDate 年份
     * @return Date
     */
    public static String getLastYearEndMonth(String strDate){
        Date date = formatDate(getYearFirstMonth(strDate),"yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(calendar.getTime());
    }


    /*是否是季度*/
    public static boolean isQuarter(String strDate){
        Date date = formatDate(strDate,"yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        if(month == Calendar.MARCH || month == Calendar.JUNE || month == Calendar.SEPTEMBER || month == Calendar.DECEMBER){
            return true;
        }
        return false;
    }

    /*季度转月*/
    public static String getMonthByQuarter(String strDate){
        //2019年第4季度----2019012
        String stringYear = strDate.substring (0,4)+'0';
        char a = strDate.charAt (6);
        String aa =null;
        if(a=='1'){
            aa="3";
        }else if(a=='2'){
            aa="6";
        }else if(a=='3'){
            aa="9";
        }else if(a=='4'){
            aa="12";
        }
        return strDate.substring (0,4)+'0'+strDate.charAt (6);
    }


    public static Date StrToDate(String str,String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
