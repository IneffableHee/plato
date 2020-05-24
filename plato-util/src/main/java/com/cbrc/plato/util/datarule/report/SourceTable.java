package com.cbrc.plato.util.datarule.report;

import com.cbrc.plato.util.math.MathUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceTable implements Serializable {
    private int bankId;
    private String bankName;
    private String table;
    private String time;
    private List<SourceCell> sourceCells;

    private String fileName;
    private String url;

    public SourceTable(String info){
        info = info.replace("{","");
        info = info.replace("}","");
        String[] infos=info.split(",");
        this.setTime(infos[0]);
        if(MathUtil.isNumber(infos[1])){
            this.setBankId(Integer.parseInt(infos[1]));
        }else{
            this.setBankName(infos[1]);
        }

        String tableCell = info.replace(infos[0]+","+infos[1]+",","");
//        System.out.println(tableCell);
        this.setTable(tableCell.substring(0,tableCell.indexOf("[")));

        this.sourceCells = new ArrayList<>();
        SourceCell sourceCell = new SourceCell(tableCell.substring(tableCell.indexOf("[")+1,tableCell.length()-1));
        this.sourceCells.add(sourceCell);
    }

    public SourceTable(){

    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<SourceCell> getSourceCells() {
        return sourceCells;
    }

    public void setSourceCells(List<SourceCell> sourceCells) {
        this.sourceCells = sourceCells;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void add(SourceTable sourceTable){
        boolean add = true;
        String cell = sourceTable.getSourceCells().get(0).getCell();
        for(SourceCell sourceCell:sourceCells){
            if(sourceCell.getCell().equals(cell)){
                add = false;
            }
        }
        if(add) {
            SourceCell sourceCell = new SourceCell(cell);
            this.sourceCells.add(sourceCell);
        }
    }

    public Set<String> getCells(){
        Set<String> cells = new HashSet<>();
        for(SourceCell sourceCell:this.getSourceCells()){
            cells.add(sourceCell.getCell());
//            System.out.println("-- "+this.table+","+sourceCell.getCell());
        }
        return cells;
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getBankId()==((SourceTable) obj).getBankId() &&
                DataRuleUtil.strEquale(this.getBankName(),((SourceTable) obj).getBankName()) &&
                DataRuleUtil.strEquale(this.getTime(),((SourceTable) obj).getTime()) &&
                DataRuleUtil.strEquale(this.getTable(),((SourceTable) obj).getTable())){
            return true;
        }
        return false;
    }

    public String getCellValue(String cell){
//        System.out.println(this.bankId+","+this.table+","+this.getTime()+cell);
        if(this.sourceCells == null){
            return null;
        }

        for(SourceCell sourceCell:this.sourceCells){
            if(sourceCell.getCell().equals(cell)){
                return sourceCell.getValue();
            }
        }
        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
