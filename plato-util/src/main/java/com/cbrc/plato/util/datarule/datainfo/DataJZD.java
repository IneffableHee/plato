package com.cbrc.plato.util.datarule.datainfo;

public class DataJZD {
    private Long id;

    private Long bankId;

    private String time;

    private String customeName;

    private String customerType;

    private String fxzh;

    private String yjzbjeb;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public String getCustomeName() {
        return customeName;
    }

    public void setCustomeName(String customeName) {
        this.customeName = customeName == null ? null : customeName.trim();
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType == null ? null : customerType.trim();
    }

    public String getFxzh() {
        return fxzh;
    }

    public void setFxzh(String fxzh) {
        this.fxzh = fxzh == null ? null : fxzh.trim();
    }

    public String getYjzbjeb() {
        return yjzbjeb;
    }

    public void setYjzbjeb(String yjzbjeb) {
        this.yjzbjeb = yjzbjeb == null ? null : yjzbjeb.trim();
    }
}