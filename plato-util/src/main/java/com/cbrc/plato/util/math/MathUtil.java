package com.cbrc.plato.util.math;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MathUtil {

    /**
     * 计算<br>
     * 步骤：1、如果有括号<br>
     * 然后取上一个最近的(坐标 计算当前括号组合里的算式 ），在继续往下查找括号 以此类推,直至循环使用到所有坐标元素
     * 计算完毕（运算顺序括号、乘除、加减）
     *
     * @param str
     * @return
     */
    public static double jisuanStr(String str) throws Exception {
//        log.info("计算"+str);
        double returnDouble = 0;
        List<String> listSplit = splitStr(str); // 拆分好的元素
        List<Integer> zKuohaoIdxList = new ArrayList<Integer>();// 左括号,<所在坐标，>
        if (Pattern.compile(".*\\(|\\).*").matcher(str).find()) {// 如果包含括号运算
            String value = "";// 单个字符值
            int zIdx = 0;// 上一个左括号在zKuoHaoIdxList的下标
            // 此层循环计算完所有括号里的算式
            List<String> tempList = new ArrayList<String>();// 前面没有计算的元素
            int removeL = 0;
            int tempListSize = 0;
            for (int i = 0; i < listSplit.size(); i++) {
                value = listSplit.get(i);
                tempList.add(value);
                tempListSize = tempList.size();
                if ("(".equals(value)) {// 左括号
                    zKuohaoIdxList.add(tempListSize-1);
                } else if (")".equals(value)) {// 遇到右括号就计算与上一左括号间的算式
                    zIdx = zKuohaoIdxList.size() - 1;// 离当前右括号最近的左括号配对
                    int start = zKuohaoIdxList.get(zIdx);
                    returnDouble = jisuan(tempList, start + 1, tempListSize-1); // 开始位置,就是上一个左括号
                    removeL = tempListSize - start;
                    tempList = removeUseList(tempList, removeL);// 移除已使用的元素
                    tempList.add(returnDouble + "");// 刚刚计算的值添加进来
                    zKuohaoIdxList.remove(zIdx);// 计算完毕清除括号
                }
            }
            // 把所有计算完
            returnDouble = jisuan(tempList, 0, tempList.size());
        } else {// 没有括号运算
            returnDouble = jisuan(listSplit, 0, listSplit.size());
        }
        return returnDouble;
    }

    /**
     * 拆分算式里的各个元素并处理对应所在位置<br>
     *
     * @param
     * @return
     */
    public static List<String> splitStr(String string) throws Exception {
        List<String> listSplit = new ArrayList<String>();
        Matcher matcher = Pattern.compile("\\-?\\d+(\\.\\d+)?|[*/()]|\\-")
                .matcher(string);// 用正则拆分成每个元素
        while (matcher.find()) {
            // System.out.println(matcher.group(0));
            listSplit.add(matcher.group(0));
        }
        return listSplit;
    }

    /**
     * 倒序删除已用过的元素
     *
     * @param list
     * @param removeLength
     *            数量
     * @return
     */
    public static List<String> removeUseList(List<String> list, int removeLength) {
        int le = list.size() - removeLength;
        for (int i = list.size() - 1; i >= le; i--) {
            list.remove(i);
        }
        return list;
    }

    /**
     * 计算算式
     *
     * @param listSplit
     * @param start
     *            括号算式开始符位置
     * @param end
     *            括号结束符位置
     * @return
     */
    public static double jisuan(List<String> listSplit, int start, int end) throws Exception {
        double returnValue = 0;
        String strValue = null;// 临时变量
        List<String> jjValueList = new ArrayList<String>();// 剩下的加减元素
        // 遍历计算乘除法
        for (int i = start; i < end; i++) {
            strValue = listSplit.get(i);
            if ("*".equals(strValue) || "/".equals(strValue)) {// 乘除
                strValue = jisuanValue("*".equals(strValue) ? "*" : "/", Double
                                .parseDouble(jjValueList.get(jjValueList.size() - 1)),
                        Double.parseDouble(listSplit.get(i + 1)))
                        + "";
                jjValueList.remove(jjValueList.size() - 1);
                i++;
            }
            jjValueList.add(strValue);
        }
        // 遍历计算加减
        for (int j = 0; j < jjValueList.size(); j++) {
            strValue = jjValueList.get(j);
            if ("-".equals(strValue) || "+".equals(strValue)) {
                returnValue = jisuanValue("-".equals(strValue) ? "-" : "+",
                        returnValue, Double.parseDouble(jjValueList.get(j + 1)));
                j++;
            } else {
                returnValue += Double.parseDouble(jjValueList.get(j));
            }
        }
        return returnValue;
    }

    /**
     * 计算2个数间的加减乘除操作 如：2*5 ，2/5
     *
     * @param type
     *            运算符
     * @param start
     *            数 相当于上面2
     * @param end
     *            被数 相当于上面5
     * @return
     */
    public static double jisuanValue(String type, double start, double end) throws Exception {
        BigDecimal a = new BigDecimal(Double.toString(start));
        BigDecimal b = new BigDecimal(Double.toString(end));

        BigDecimal d = null;
        if ("-".equals(type)) {
            d = a.subtract(b).setScale(5,BigDecimal.ROUND_HALF_UP);
//            d = start - end;
        } else if ("+".equals(type)) {
            d = a.add(b).setScale(5,BigDecimal.ROUND_HALF_UP);
//            d = start + end;
        } else if ("*".equals(type)) {
            d = a.multiply(b).setScale(5,BigDecimal.ROUND_HALF_UP);
//            d = start * end;
        } else if ("/".equals(type)) {
            if (0 == start || 0 == end) {
                d = new BigDecimal(0);
            }else {
                d = a.divide(b,5,BigDecimal.ROUND_HALF_UP);
//                d = start / end;
            }
        }
        return d.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static  String wanyuanToyi(String value){
        double d = Double.parseDouble(value)/10000;
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(d);
    }

    public static String liangweixiaoshu(String value){
        double d = Double.parseDouble(value);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new BigDecimal(String.valueOf (d)).toString ();
    }

    public static String xiaoshuweishu(String value,int weishu){
        double d = Double.parseDouble(value);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(weishu, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new BigDecimal(String.valueOf (d)).toPlainString ();
    }

    public static String keepDecimalLength(String value,int length){
        double d = Double.parseDouble(value);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(length, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new BigDecimal(String.valueOf (d)).toString ();
    }

    public static boolean isNumber(String str) {
        if(str==null||str==" ")
            return false;
        if(countStr(str,"/")>1||countStr(str,"%")>1||countStr(str,".")>1)
            return false;
        if(str.contains("+")||str.contains("-")||str.contains("*")||str.contains("(")||str.contains(")")){
            return false;
        }

//        System.out.println(countStr(str,"/"));
        if(str.contains("/")){
            String [] sstr = str.split("/");
            try {
                Double.parseDouble(sstr[0]);
                Double.parseDouble(sstr[1]);
            }catch (Exception e){
                return false;
            }
            return true;
        }
        if(str.endsWith("%")&&countStr(str,"%")==1){
            try {
                Double.parseDouble(str.substring(0,str.length()-1));
            }catch (Exception e){
                return false;
            }
            return true;
        }

        try {
            Double.parseDouble(str);
        }catch (Exception e){
            return false;
        }
        return str.matches("-?[0-9]+.*[0-9]*");
    }

    /*解析NAN、Infinity*/
    public static  String isNanInfinity(String number){
        if(number.equals("NaN")){
            return String.valueOf(0.00);
        }else if(number.equals("Infinity")){
            return String.valueOf(1.00);
        }else if(number.equals("-Infinity")){
            return String.valueOf(-1.00);
        }else if(number .equals ("null")){
            return "null";
        }else if(number.equals ("NullValue")){
            return "0";
        }
        else {
            return keepDecimalLength(number,4);
        }
    }

    /*判断字符出现次数*/
    public static int countStr(String str,String sToFind) {
        int num = 0;
        while (str.contains(sToFind)) {
            str = str.substring(str.indexOf(sToFind) + sToFind.length());
            num ++;
        }
        return num;
    }

    /*判断对错*/
    public static boolean TOF(String a,String b,String term,Double accuracy){
        if(!MathUtil.isNumber(a)||!MathUtil.isNumber(b)){
            return false;
        }
        accuracy = accuracy==null?0:accuracy;
        log.info("start:"+a+","+b);
        double da,db;
        if(a.contains("/")){
            da = Double.valueOf(a.substring(0,a.indexOf("/")))/Double.valueOf(a.substring(a.indexOf("/")+1,a.length()));
        }else if(a.contains("%")){
            da = Double.valueOf(a.substring(0,a.length()-1))/100;
        }else {
            da = Double.valueOf(a);
        }

        if(b.contains("/")){
            db = Double.valueOf(b.substring(0,b.indexOf("/")))/Double.valueOf(b.substring(b.indexOf("/")+1,a.length()));
        }else if(a.contains("%")){
            db = Double.valueOf(b.substring(0,b.length()-1))/100;
        }else {
            db = Double.valueOf(b);
        }
        log.info("end:"+da+","+db);
        if(term.startsWith("=")){
            double abs = Math.abs(da-db);
            BigDecimal bg = new BigDecimal(abs);
            double value = bg.setScale(String.valueOf(accuracy).length(), BigDecimal.ROUND_HALF_UP).doubleValue();
            log.info(String.valueOf(value));
            if(value<=accuracy){
                return true;
            }
        }else if(term.startsWith(">")){
            if(da>db){
                return true;
            }
        }else if(term.startsWith("<")){
            if(da<db){
                return true;
            }
        }else if(term.startsWith("≥")){
            if(da>=db){
                return true;
            }
        }else if(term.startsWith("≤")){
            if(da<=db){
                return true;
            }
        }
        return false;
    }

    public static boolean SubjectTOF(List<String> subjects,String rule,String target,Double accuracy){
        log.info(rule+target);
        if(!rule.equals("=")&& target == null){
            return false;
        }
        if(!rule.equals("=")&& StringUtils.isBlank(target)){
            return false;
        }
        if(!StringUtils.isBlank(target)&&!MathUtil.isNumber(target)){
            return false;
        }

        if(rule.equals("=")&&StringUtils.isBlank(target)){
            String subjectValue = null;
            for(String sunbject:subjects){
                if(StringUtils.isBlank(sunbject)){
                    return false;
                }
                if(StringUtils.isBlank(subjectValue)){
                    subjectValue = sunbject;
                }else{
                    if(!TOF(sunbject,subjectValue,"=",accuracy))
                        return false;
                }
            }
            return true;
        }

        for(String sunbject:subjects){
            if(StringUtils.isBlank(sunbject)){
                return false;
            }
            if(!TOF(sunbject,target,rule,accuracy))
                return false;
        }
        return true;
    }

    /*判断对错*/
    public static boolean TOF(String a,String b,String term){
        if(!MathUtil.isNumber(a)||!MathUtil.isNumber(b)){
            return false;
        }
        log.info("start:"+a+","+b);
        double da,db;
        if(a.contains("/")){
            da = Double.valueOf(a.substring(0,a.indexOf("/")))/Double.valueOf(a.substring(a.indexOf("/")+1,a.length()));
        }else if(a.contains("%")){
            da = Double.valueOf(a.substring(0,a.length()-1))/100;
        }else {
            da = Double.valueOf(a);
        }

        if(b.contains("/")){
            db = Double.valueOf(b.substring(0,b.indexOf("/")))/Double.valueOf(b.substring(b.indexOf("/")+1,a.length()));
        }else if(a.contains("%")){
            db = Double.valueOf(b.substring(0,b.length()-1))/100;
        }else {
            db = Double.valueOf(b);
        }
        log.info("end:"+da+","+db);
        if(term.startsWith("=")){
            if(da==db){
                return true;
            }
        }else if(term.startsWith(">")){
            if(da>db){
                return true;
            }
        }else if(term.startsWith("<")){
            if(da<db){
                return true;
            }
        }else if(term.startsWith("≥")){
            if(da>=db){
                return true;
            }
        }else if(term.startsWith("≤")){
            if(da<=db){
                return true;
            }
        }
        return false;
    }
}
