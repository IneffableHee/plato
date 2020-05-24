package com.cbrc.plato.util.datarule.report;

public class ReportCell {
    private String cell;    //所属单元格
    private int month;  //1:月报、0:季报
    private String tatalSource;  //规则字段（全）
    private String ruleSource;     //取数规则字段（去除头尾）
    private String dataRule;  //取数规则
    private String expressionRule;  //解析出来的规则
    private String warningRule;     //警告规则
    private String excludeRule;    //不取数规则
    private String bankName;    //取数机构
    private int bankId;     //取数机构
    private String value;   //值
    private boolean warning;       //是否警告
    private String valueType;   //值数据类型
    private String type;    //单元格类型

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public int isMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDataRule() {
        return dataRule;
    }

    public void setDataRule(String dataRule) {
        this.dataRule = dataRule;
    }

    public String getExcludeRule() {
        return excludeRule;
    }

    public void setExcludeRule(String excludeRule) {
        this.excludeRule = excludeRule;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public String getWarningRule() {
        return warningRule;
    }

    public void setWarningRule(String warningRule) {
        this.warningRule = warningRule;
    }

    public String getTatalSource() {
        return tatalSource;
    }

    public void setTatalSource(String tatalSource) {
        this.tatalSource = tatalSource;
    }

    public String getRuleSource() {
        return ruleSource;
    }

    public void setRuleSource(String ruleSource) {
        this.ruleSource = ruleSource;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getExpressionRule() {
        return expressionRule;
    }

    public void setExpressionRule(String expressionRule) {
        this.expressionRule = expressionRule;
    }

    public void splitRule(){
        String expressionRule = this.expressionRule;
        if(expressionRule!=null&&expressionRule.contains("$")){
            this.warningRule = expressionRule.substring(expressionRule.indexOf("$")+1);
            expressionRule = expressionRule.substring(0,expressionRule.indexOf("$"));
        }
        this.dataRule = expressionRule;
    }
}
