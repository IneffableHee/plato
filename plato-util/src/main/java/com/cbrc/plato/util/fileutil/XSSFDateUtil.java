package com.cbrc.plato.util.fileutil;

import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Calendar;

/**
 * @author fangtao
 * @date 2019-04-12
 */
public class XSSFDateUtil  extends DateUtil{
    protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
        return DateUtil.absoluteDay(cal, use1904windowing);
    }

}
