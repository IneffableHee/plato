package com.cbrc.plato.core.basic.model;

public class ReportCell {
    private int mould;  //所属模板
    private String cell;    //所属单元格
    private int month;  //1:月报、0:季报
    private String dataRule;  //取数规则
    private boolean warning;       //是否警告
    private String warningRule;     //警告规则
    private String warningBorder;   //警告边框
    private String warningColor;    //警告文字颜色
    private String warningBGColor;  //警告背景色
    private String excludeRule;    //不取数规则
    private String bankName;    //取数机构
    private String value;   //值
    private String valueType;   //值数据类型
    private String type;    //单元格类型

    private String tableForSource;// 来源表
    private String cellForSource;//单元格 来源

    public String getTableForSource() {
        return tableForSource;
    }

    public void setTableForSource(String tableForSource) {
        this.tableForSource = tableForSource;
    }

    public String getCellForSource() {
        return cellForSource;
    }

    public void setCellForSource(String cellForSource) {
        this.cellForSource = cellForSource;
    }

    public int getMould() {
        return mould;
    }

    public void setMould(int mould) {
        this.mould = mould;
    }

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

    public String getWarningRule() {
        return warningRule;
    }

    public void setWarningRule(String warningRule) {
        this.warningRule = warningRule;
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
}
