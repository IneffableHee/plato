package com.cbrc.plato.util.utilpo;

public class ExcelInfo {

    private int id;

    private int bankId;     //银行机构ID

    private String bankName;//银行机构名

    private String filePath;//文件路径

    private String fileName;//文件名

    private String excelCode;//表编号（G01）

    private String excelType;  //表类型（月报、季报）

    private String excelTime;//表格期次

    private String author;  //所有者

    private int authorId;   //所有者

    private String fileSurface;//文件所属编码中文名称


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExcelCode() {
        return excelCode;
    }

    public void setExcelCode(String excelCode) {
        this.excelCode = excelCode;
    }

    public String getExcelType() {
        return excelType;
    }

    public void setExcelType(String excelType) {
        this.excelType = excelType;
    }

    public String getExcelTime() {
        return excelTime;
    }

    public void setExcelTime(String excelTime) {
        this.excelTime = excelTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getFileSurface() {
        return fileSurface;
    }

    public void setFileSurface(String fileSurface) {
        this.fileSurface = fileSurface;
    }
}
