package com.cbrc.plato.util.utilpo;

/**
 * @author fangtao
 * @date 2019-04-12
 */
public class FileModel {
    private int id;
    private String fileName;//文件名
    private String fileCode;//文件所述编码
    private String filePath;//文件绝对路径
    private String fileSurface;//文件所属编码中文名称

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileSurface() {
        return fileSurface;
    }

    public void setFileSurface(String fileSurface) {
        this.fileSurface = fileSurface;
    }
}
