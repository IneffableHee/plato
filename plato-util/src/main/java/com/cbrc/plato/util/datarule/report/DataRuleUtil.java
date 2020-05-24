package com.cbrc.plato.util.datarule.report;

import com.cbrc.plato.util.datarule.expression.IExpression;
import com.cbrc.plato.util.datarule.expression.RuleExpression;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.math.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DataRuleUtil {
    public static final String OPRATION="(avgy|hbzzl|tbzzl|nc|sy|inc|sqs|max|ncs|nczzl|tb|hb|tqs|qntqzl|cklsl)";    //运算公式
    public static final String MATH_SYMBOL="[+\\-*/()]";    //运算符号
    public static final String RULE_SYMBOL="[+\\-*/()$#≥≤=<>!]";  //规则符号
    public static final String EXPRESSION_CELL = "\\{.*?}";     //规则单个单元，以{开始，以}结束
    public static final String SINGLE_CELL = "[a-zA-Z]+\\d+?.*?\\[[a-zA-Z]+\\d+.*?\\]";       //匹配单个单元格
    public static final String NUMBER = "^-?[0-9]+.*[0-9]*$|^\\d+\\.?\\d*\\%?$";

    static String operation = "avgy<.*?>|hbzzl<.*?>|tbzzl<.*?>|znxs|nc<.*?>|sy<.*?>|inc<.*?>";
    static String normalSplit = "avgy<.*?>|hbzzl<.*?>|tbzzl<.*?>|znxs|nc<.*?>|sy<.*?>|inc<.*?>|[+\\-*/()]";

    /*字符规范化*/
    public static String symbolNormalization(String src){
        if(src==null||src==""||src==" ")
            return src;
        src = src.replace(" ","");
        src = src.replace("·",".");
        src = src.replace("，",",");
        src = src.replace("【","[");
        src = src.replace("】","]");
        src = src.replace("<=","≤");
        src = src.replace(">=","≥");
        src = src.replace("＜","<");
        src = src.replace("＞",">");
        src = src.replace("！","!");
        src = src.replace("＋","+");
        src = src.replace("－","-");
        src = src.replace("×","*");
        src = src.replace("÷","/");
        src = src.replace("＝","=");
        src = src.replace("≠","!=");
        src = src.replace("Ⅰ","I");
        src = src.replace("Ⅱ","II");
        src = src.replace("Ⅲ","III");
        src = src.replace("Ⅳ","IV");
        src = src.replace("Ⅴ","V");
        src = src.replace("Ⅵ","VI");
        src = src.replace("Ⅶ","VII");
        src = src.replace("Ⅷ","VIII");
        src = src.replace("Ⅸ","IX");
        src = src.replace("Ⅹ","X");
        return src;
    }

    public static String parse(String rule,String time,String bank){
        IExpression expression = new RuleExpression(rule,time,bank);
        return expression.interpret();
    }

    public static List<SourceTable> getSourceTable(ReportTable reportTable){
        for(ReportCell reportCell:reportTable.getReportCells()){
            String rule = reportCell.getExpressionRule();
            System.out.println("rule:"+rule);
            Matcher omatcher = Pattern.compile(EXPRESSION_CELL).matcher(rule);
            while (omatcher.find()) {
                String operation = omatcher.group(0);
//                System.out.println("cell:"+operation);
            }
        }
        return null;
    }

    public static List<ReportCell> getReportCells(String path, int sheet) {
        Map<String,String> rules = null;
        try {
            rules = ExcelRead.readReportMouldCells(path,0);
        } catch (Exception e) {
            log.info("模板表错误，无法读取取数规则！");
        }

        if(rules==null||rules.size()==0)
            log.info("模板表错误，无法读取取数规则！");

        List<ReportCell> reportCells = new ArrayList<>();
        for(Map.Entry<String, String> entry : rules.entrySet()){
            ReportCell reportCell = new ReportCell();

            String mapKey = entry.getKey();
            String mapValue = symbolNormalization(entry.getValue());

            reportCell.setCell(mapKey);
            reportCell.setTatalSource(mapValue);
            if(mapValue.indexOf("~")!=-1){
                String[] strArray = mapValue.split("~");
                if(strArray.length!=2){
                    log.info(mapKey+"取数规则错误："+mapValue);
                    return null;
                }
                reportCell.setBankName(strArray[1]);
                mapValue = strArray[0];
            }
            if(mapValue.indexOf("#")!=-1){
                String[] strArray = mapValue.split("#");
                if(strArray.length!=2){
                    log.info(mapKey+"取数规则错误："+mapValue);
                    return null;
                }
                reportCell.setExcludeRule(strArray[1]);
                mapValue = strArray[0];
            }

            if(mapValue.indexOf(",")!=-1){
                String[] strArray = mapValue.split(",");
                if(reportCell.getExcludeRule()==null){
                    reportCell.setRuleSource(mapValue.substring(6,mapValue.length()));
                }else{
                    reportCell.setRuleSource(mapValue.substring(6,mapValue.length())+"#"+reportCell.getExcludeRule());
                }
                if(strArray[0].equals("@js-j")){
                    reportCell.setMonth(0);
                }else{
                    reportCell.setMonth(1);
                }
            }
            reportCells.add(reportCell);
        }
//        parseReportCell(reportCells);
        return reportCells;
    }

    public static StringTokenizer splitSourceRule(String rule){
        return new StringTokenizer(rule, DataRuleUtil.RULE_SYMBOL.replace("[","").replace("]",""), true);
    }

    public static boolean strEquale(String str1,String str2){
        if(str1 == null&str2 == null){
            return true;
        }else if(str1.equals(str2)){
            return true;
        }
        return false;
    }

    public static boolean isDataRuleMix(String str){
        String ss = str;
        if(ss == null||ss == "")
            return false;

        String regx = DataRuleUtil.RULE_SYMBOL;
        String [] dataStr = ss.split(regx);
        for(String s:dataStr){
            if(s.isEmpty())
                continue;

            if(DataRuleUtil.OPRATION.contains(s))
                continue;

            if(MathUtil.isNumber(s))
                continue;

            if(!s.endsWith("]"))
                return false;
            if(s.startsWith("["))
                return false;

            String s1 = s.replace("[","");
            String s2 = s.replace("]","");
            if(s.length()-s1.length()!=1)
                return false;
            if (s.length()-s2.length()!=1)
                return false;

            if(!Pattern.matches(SINGLE_CELL, s))
                return false;
        }

        return true;
    }

    public static boolean isDataRule(String str){
        if(str == null||str == "")
            return false;

        return Pattern.matches(".*"+SINGLE_CELL+".*", str);
    }
}
