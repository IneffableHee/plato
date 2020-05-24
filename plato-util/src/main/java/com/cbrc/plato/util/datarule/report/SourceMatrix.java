package com.cbrc.plato.util.datarule.report;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SourceMatrix implements Serializable {
    private List<SourceTable> sourceTables;
    private List<SourceTable> notUplaod;
    private String bankName;    //机构名称，审表分机构用
    private String time;    //期次，审表用

    public SourceMatrix(){

    }

    public SourceMatrix(ReportTable reportTable){
        this.sourceTables = new ArrayList<>();
        this.notUplaod = new ArrayList<>();
        this.time = reportTable.getTime();
        for(ReportCell reportCell:reportTable.getReportCells()){
            String rule = reportCell.getExpressionRule();
            log.info("SourceMatrix "+rule);
            if(reportCell.getTatalSource()!=null){
                if(reportCell.getTatalSource().contains("{{name}}")||reportCell.getTatalSource().contains("{{time}}"))
                    continue;
            }
            if(rule!=null&&!rule.isEmpty()){
                Matcher omatcher = Pattern.compile(DataRuleUtil.EXPRESSION_CELL).matcher(rule);
                while (omatcher.find()) {
                    String cell = omatcher.group(0);
                    System.out.println("cell:"+cell);
                    SourceTable sourceTable = new SourceTable(cell);
                    addSourceTables(sourceTable);
                }
            }
        }
    }

    public SourceMatrix(ReportCell reportCell){
        String rule = reportCell.getExpressionRule();
    }

    public List<SourceTable> getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(List<SourceTable> sourceTables) {
        this.sourceTables = sourceTables;
    }

    public void addSourceTables(SourceTable sourceTable){
        int add = 0;
        for(SourceTable sourceTable1:this.sourceTables){
            if(sourceTable1.equals(sourceTable)){
                sourceTable1.add(sourceTable);
                add = 1;
            }
        }
        if(add == 0){
            this.sourceTables.add(sourceTable);
        }
    }

    public boolean hasSourceTable(SourceTable sourceTable){
        for(SourceTable sourceTable1:this.sourceTables){
            if(sourceTable1.equals(sourceTable)){
                return true;
            }
        }
        return false;
    }

    public String getCellValue(String expressionCell){
        expressionCell=expressionCell.substring(1,expressionCell.length()-1);
        String[] arr = expressionCell.split(",");
        int bank = Integer.parseInt(arr[1]);
        String time = arr[0];
        expressionCell = expressionCell.substring(arr[0].length()+arr[1].length()+2);
        if(!expressionCell.contains("[")||!expressionCell.contains("]")){
            return null;
        }
        String table = expressionCell.substring(0,expressionCell.indexOf("["));
        String cell = expressionCell.substring(expressionCell.indexOf("[")+1,expressionCell.lastIndexOf("]"));

//        System.out.println("table:"+table+",cell:"+cell+",bank:"+bank+",time:"+time);
        for(SourceTable sourceTable:this.sourceTables){
            if(sourceTable.getTable().equals(table)&&sourceTable.getBankId()==bank&&sourceTable.getTime().equals(time)){
                String value = sourceTable.getCellValue(cell);
                return value;
            }
        }
        return null;
    }

    public List<SourceTable> getNotUplaod() {
        return notUplaod;
    }

    public void setNotUplaod(List<SourceTable> notUplaod) {
        this.notUplaod = notUplaod;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public SourceTable getLocalSourceTableByTimeAndCode(String time,String code){
        log.info("getLocalSourceTableByTimeAndCode length:"+this.sourceTables.size()+","+time+","+code);
        for(SourceTable sourceTable:this.sourceTables){
//            System.out.println("getLocalSourceTableByTimeAndCode:"+sourceTable.getTime()+"-"+sourceTable.getBankName()+"-"+sourceTable.getTable());
            if(sourceTable.getTime()!=null&&sourceTable.getTime().equals(time)&&
                    sourceTable.getTable()!=null&&sourceTable.getTable().equals(code)){
                System.out.println("get table:"+sourceTable.getUrl());
                return sourceTable;
            }
        }
        return null;
    }

    public String getCheckRuleCellValue(String ruleCell){
        System.out.println("getCheckRuleCellValue:"+ruleCell);
        ruleCell=ruleCell.substring(1,ruleCell.length()-1);
        String[] arr = ruleCell.split(",");
        String bank = arr[1];
        String time = arr[0];
        ruleCell = ruleCell.substring(arr[0].length()+arr[1].length()+2);
        if(bank==null||!ruleCell.contains("[")||!ruleCell.contains("]")){
            return null;
        }
        String table = ruleCell.substring(0,ruleCell.indexOf("["));
        String cell = ruleCell.substring(ruleCell.indexOf("[")+1,ruleCell.lastIndexOf("]"));

//        System.out.println("table:"+table+",cell:"+cell+",bank:"+bank+",time:"+time);
        for(SourceTable sourceTable:this.sourceTables){
            if(sourceTable.getTable().equals(table)&&sourceTable.getBankName().equals(bank)&&sourceTable.getTime().equals(time)){
                String value = sourceTable.getCellValue(cell);
                return value;
            }
        }



        System.out.println("table:"+table+",cell:"+cell+",bank:"+this.getBankName()+",time:"+this.getSourceTables().get(0).getTime());
//        for(SourceTable sourceTable:this.sourceTables){
//            if(sourceTable.getTable().equals(table)){
//                String value = sourceTable.getCellValue(cell);
//                return value;
//            }
//        }
        return null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
