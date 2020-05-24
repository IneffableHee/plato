package com.cbrc.plato.core.basic.model;

public class ExcelFileForCount {
    private String bankName;

    private Integer bankId;

    private String time;

    private Integer totals;

    private Integer upload;

    private Integer noUpload;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getTotals() {
        return totals;
    }

    public void setTotals(Integer totals) {
        this.totals = totals;
    }

    public Integer getUpload() {
        return upload;
    }

    public void setUpload(Integer upload) {
        this.upload = upload;
    }

    public Integer getNoUpload() {
        return noUpload;
    }

    public void setNoUpload(Integer noUpload) {
        this.noUpload = noUpload;
    }
}
