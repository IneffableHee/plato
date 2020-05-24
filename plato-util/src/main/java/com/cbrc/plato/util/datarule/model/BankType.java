package com.cbrc.plato.util.datarule.model;

public class BankType {
    private Integer id;

    private String bankType;

    private String banks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType == null ? null : bankType.trim();
    }

    public String getBanks() {
        return banks;
    }

    public void setBanks(String banks) {
        this.banks = banks == null ? null : banks.trim();
    }
}