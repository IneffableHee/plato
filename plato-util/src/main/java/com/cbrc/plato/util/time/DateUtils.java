/**
 * Author:   Xian
 * FileName: DateUtils
 * Date:     2019/2/21 15:57
 */
package com.cbrc.plato.util.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
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
