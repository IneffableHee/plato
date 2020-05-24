package com.cbrc.plato.util.datarule.report;

import com.cbrc.plato.util.math.MathUtil;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ReportTable {
    private int mould;  //所属模板
    private String time;   //期次
    private String warningBorder;   //警告边框
    private String warningColor;    //警告文字颜色
    private String warningBGColor;  //警告背景色
    private List<ReportCell> reportCells;

    public ReportTable(){

    }

    public ReportTable(String path,int sheet){
        reportCells = DataRuleUtil.getReportCells(path,sheet);
    }

    public int getMould() {
        return mould;
    }

    public void setMould(int mould) {
        this.mould = mould;
    }

    public String getWarningBorder() {
        return warningBorder;
    }

    public void setWarningBorder(String warningBorder) {
        this.warningBorder = warningBorder;
    }

    public String getWarningColor() {
        return warningColor;
    }

    public void setWarningColor(String warningColor) {
        this.warningColor = warningColor;
    }

    public String getWarningBGColor() {
        return warningBGColor;
    }

    public void setWarningBGColor(String warningBGColor) {
        this.warningBGColor = warningBGColor;
    }

    public List<ReportCell> getReportCells() {
        return reportCells;
    }

    public void setReportCells(List<ReportCell> reportCells) {
        this.reportCells = reportCells;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAllCellsBank(int bank){
        for(ReportCell reportCell:this.getReportCells()){
            reportCell.setBankId(bank);
        }
    }

    public void expressionDataRulewithBankId(){
        for(ReportCell reportCell:this.getReportCells()){
            String cellRule = reportCell.getRuleSource();
            if(reportCell.getTatalSource()!=null){
                if(reportCell.getTatalSource().contains("{{name}}")||reportCell.getTatalSource().contains("{{time}}"))
                    continue;
            }

            if(cellRule!=null&&!cellRule.isEmpty()){
//                log.info(reportCell.getCell()+","+reportCell.getRuleSource());
                int tip = 0;
                if(cellRule.contains("G4A-1(a)")){
                    cellRule = cellRule.replace("G4A-1(a)","G4A1a");
                    tip=1;
                }
                String expressionRule = DataRuleUtil.parse(cellRule,this.getTime(),String.valueOf(reportCell.getBankId()));
                if(tip==1){
                    expressionRule = expressionRule.replace("G4A1a","G4A-1(a)");
                }
                reportCell.setExpressionRule(expressionRule);
//                log.info("--parsed:"+expressionRule);
            }
        }
    }

    public void expressionDataRulewithBankName(){
        for(ReportCell reportCell:this.getReportCells()){
            String cellRule = reportCell.getRuleSource();
            if(cellRule!=null&&!cellRule.isEmpty()){
                log.info(reportCell.getCell()+","+reportCell.getRuleSource());
                int tip = 0;
                if(cellRule.contains("G4A-1(a)")){
                    cellRule = cellRule.replace("G4A-1(a)","G4A1a");
                    tip=1;
                }
                String expressionRule = DataRuleUtil.parse(cellRule,this.getTime(),reportCell.getBankName());
                if(tip==1){
                    expressionRule = expressionRule.replace("G4A1a","G4A-1(a)");
                }
                reportCell.setExpressionRule(expressionRule);
                log.info("--parsed:"+expressionRule);
            }
        }
    }
    public void setCreateUserAndTime(String user,String time){
        System.out.println("setCreateUserAndTime ");
        for(ReportCell reportCell:this.getReportCells()){
            if(reportCell.getTatalSource()==null)
                continue;
            if(reportCell.getTatalSource().contains("{{name}}")){
                System.out.println("setCreateUser:"+reportCell.getCell()+user);
                String value = reportCell.getTatalSource().replace("{{name}}",user);
                reportCell.setValue(value);
                continue;
            }
            if(reportCell.getTatalSource().contains("{{time}}")){
                System.out.println("setCreateTime:"+reportCell.getCell()+time);
                String value = reportCell.getTatalSource().replace("{{time}}",time);
                reportCell.setValue(value);
                continue;
            }
        }
    }

    public void setCellValues(SourceMatrix sourceMatrix){
        Pattern number = Pattern.compile(DataRuleUtil.NUMBER);
        FelEngine fel = new FelEngineImpl();
        for(ReportCell reportCell:this.getReportCells()){
            reportCell.splitRule();
            String dataRule = reportCell.getDataRule();
            if(reportCell.getTatalSource()!=null){
                if(reportCell.getTatalSource().contains("{{name}}")||reportCell.getTatalSource().contains("{{time}}"))
                    continue;
            }
            String warningRule = reportCell.getWarningRule();
//            log.info("sourceRule:"+reportCell.getRuleSource()+"dataRule:"+dataRule+" , warningRule:"+warningRule);
            if(dataRule!=null&&dataRule.contains("{")){
                log.info("dataRule:"+dataRule);
                String evalStr = dataRule;
                reportCell.splitRule();
                Matcher dmatcher = Pattern.compile(DataRuleUtil.EXPRESSION_CELL).matcher(dataRule);
                while (dmatcher.find()) {
                    String cell = dmatcher.group(0);
                    String value = sourceMatrix.getCellValue(cell);
                    System.out.println("cell:"+cell+",value:"+value);
                    if(value!=null&&!value.isEmpty()){
                        evalStr = evalStr.replace(cell,value);
                    }else{
                        evalStr = null;
                        break;
                    }
                }

                if(evalStr == null||evalStr.isEmpty()){
                    log.info("NullValue:"+evalStr);
                    reportCell.setValue("NullValue");
                    continue;
                }else if(evalStr.contains("notUpload")){
                    log.info("notUpload:"+evalStr);
                    reportCell.setValue("notUpload");
                    continue;
                }else if(evalStr.contains("ruleError")){
                    log.info("ruleError:"+evalStr);
                    reportCell.setValue("ruleError");
                    continue;
                }

                log.info("data fel :"+evalStr);
                try {
                    String result = String.valueOf(fel.eval(evalStr));
                    String pResult = MathUtil.xiaoshuweishu(result,5);
                    log.info("data result:"+result+","+pResult);
                    reportCell.setValue(pResult);
                }catch (Exception e){
                    log.info("data fel fault:"+evalStr);
                    continue;
                }

            }

            if(reportCell.getValue()==null)
                continue;

            String warnValue = "";
            double dValue = 0;
            if(warningRule!=null&&warningRule.contains("{")){
                String evalStr = warningRule.substring(warningRule.indexOf("{"));
                reportCell.splitRule();
                Matcher wmatcher = Pattern.compile(DataRuleUtil.EXPRESSION_CELL).matcher(warningRule);
                while (wmatcher.find()) {
                    String cell = wmatcher.group(0);
                    String value = sourceMatrix.getCellValue(cell);
//                    System.out.println("warningRule cell value:"+value);
                    if(value!=null){
                        evalStr = evalStr.replace(cell,value);
                    }
                }
                log.info("warning fel :"+evalStr);
                warnValue = String.valueOf(fel.eval(evalStr));
                log.info("warning result:"+warnValue);
            }

            if(warningRule!=null&&warningRule.contains("/")){
                warnValue = String.valueOf(fel.eval(warningRule.replaceAll(">|<|≥|≤|!=|=", "")));
            }

            if(warningRule!=null&&warningRule.startsWith("!=")){
                warnValue = warnValue.isEmpty()?warningRule.substring(2):warnValue;
                if(number.matcher(warnValue).matches()){
                    if(warnValue.contains("%")){
                        dValue = Double.valueOf(warnValue.substring(0,warnValue.length()-1))/100;
                    }else {
                        dValue = Double.valueOf(warnValue);
                    }
                    if(Double.valueOf(reportCell.getValue())!=dValue){
                        reportCell.setWarning(true);
                    }
                }else{
                    System.out.println("规则错误！!"+warnValue);
                }

            }else if(warningRule!=null){
                System.out.println("warningRule:"+warningRule);
                warnValue = warnValue.isEmpty()?warningRule.substring(1):warnValue;
                if(number.matcher(warnValue).matches()){
                    if(warnValue.contains("%")){
                        dValue = Double.valueOf(warnValue.substring(0,warnValue.length()-1))/100;
                    }else {
                        dValue = Double.valueOf(warnValue);
                    }
                }else{
                    System.out.println("规则错误！!"+warnValue);
                }
                
                if(warningRule.startsWith("=")){
                    if(Double.valueOf(reportCell.getValue())==dValue){
                        reportCell.setWarning(true);
                    }
                }else if(warningRule.startsWith(">")){
                    if(Double.valueOf(reportCell.getValue())>dValue){
                        reportCell.setWarning(true);
                    }
                }else if(warningRule.startsWith("<")){
                    if(Double.valueOf(reportCell.getValue())<dValue){
                        reportCell.setWarning(true);
                    }
                }else if(warningRule.startsWith("≥")){
                    if(Double.valueOf(reportCell.getValue())>=dValue){
                        reportCell.setWarning(true);
                    }
                }else if(warningRule.startsWith("≤")){
                    System.out.println("getValue:"+reportCell.getValue()+",dValue:"+dValue);
                    if(Double.valueOf(reportCell.getValue())<=dValue){
                        reportCell.setWarning(true);
                    }
                }
            }
        }
    }
}
