package com.cbrc.plato.core.basic.model;

public class CellOperation {
    private String operation;
    private String excludeRule;
    private String value;
    private int month;  //1:月报、0:季报

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getExcludeRule() {
        return excludeRule;
    }

    public void setExcludeRule(String excludeRule) {
        this.excludeRule = excludeRule;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
