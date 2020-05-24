package com.cbrc.plato.core.basic.model;

public class CheckTable {

    private int id;
    private int mouldId;  //所属模板
    private String  checkRule ;//校验规则
    private String  checkTarget;//校验目标
    private String  toCheckTarget; //待校验目标
    private String  checkResults;//校验结果
    private String  wuIsPoor;//吴差范围
    private int     isWarning; //是否警告
    private int      month;  //1:月报、0:季报
    private String  toCheckTargetValue;//待校验目标  值
    private String  checkTargetValue;//校验目标 值
    private String  projectName;//项目名称
    private String  projectTargetName;//目标项目名称
    private String  toTableForSource;// 待校验表 来源表
    private String  tableForSource;//目标表 来源
    private String  bankName ;
    private String  toTableForSourceCell;// 待校验表 来源表 单元格
    private String  tableForSourceCell;//目标表 来源       单元格

    private String userName;

    private String param;
    private String param1;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMouldId() {
        return mouldId;
    }

    public void setMouldId(int mouldId) {
        this.mouldId = mouldId;
    }

    public String getCheckRule() {
        return checkRule;
    }

    public void setCheckRule(String checkRule) {
        this.checkRule = checkRule;
    }

    public String getCheckTarget() {
        return checkTarget;
    }

    public void setCheckTarget(String checkTarget) {
        this.checkTarget = checkTarget;
    }

    public String getToCheckTarget() {
        return toCheckTarget;
    }

    public void setToCheckTarget(String toCheckTarget) {
        this.toCheckTarget = toCheckTarget;
    }

    public String getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(String checkResults) {
        this.checkResults = checkResults;
    }

    public String getWuIsPoor() {
        return wuIsPoor;
    }

    public void setWuIsPoor(String wuIsPoor) {
        this.wuIsPoor = wuIsPoor;
    }

    public int getIsWarning() {
        return isWarning;
    }

    public void setIsWarning(int isWarning) {
        this.isWarning = isWarning;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getToCheckTargetValue() {
        return toCheckTargetValue;
    }

    public void setToCheckTargetValue(String toCheckTargetValue) {
        this.toCheckTargetValue = toCheckTargetValue;
    }

    public String getCheckTargetValue() {
        return checkTargetValue;
    }

    public void setCheckTargetValue(String checkTargetValue) {
        this.checkTargetValue = checkTargetValue;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectTargetName() {
        return projectTargetName;
    }

    public void setProjectTargetName(String projectTargetName) {
        this.projectTargetName = projectTargetName;
    }

    public String getToTableForSource() {
        return toTableForSource;
    }

    public void setToTableForSource(String toTableForSource) {
        this.toTableForSource = toTableForSource;
    }

    public String getTableForSource() {
        return tableForSource;
    }

    public void setTableForSource(String tableForSource) {
        this.tableForSource = tableForSource;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getToTableForSourceCell() {
        return toTableForSourceCell;
    }

    public void setToTableForSourceCell(String toTableForSourceCell) {
        this.toTableForSourceCell = toTableForSourceCell;
    }

    public String getTableForSourceCell() {
        return tableForSourceCell;
    }

    public void setTableForSourceCell(String tableForSourceCell) {
        this.tableForSourceCell = tableForSourceCell;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
