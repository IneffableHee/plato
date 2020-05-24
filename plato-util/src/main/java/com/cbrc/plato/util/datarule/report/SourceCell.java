package com.cbrc.plato.util.datarule.report;

public class SourceCell {
    private String cell;
    private String value;

    public SourceCell(String cell){
        this.cell = cell;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
