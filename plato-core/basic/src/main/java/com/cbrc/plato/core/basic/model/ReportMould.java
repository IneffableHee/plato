package com.cbrc.plato.core.basic.model;

import java.util.Date;

public class ReportMould {
    private Integer id;

    private String mouldName;

    private String mouldPath;

    private String mouldDescribe;

    private String webFile;

    private Integer author;

    private Date createTime;

    private Date updateTime;

    private Integer department;

    private Integer mouldShow;

    private Integer mouldStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return mouldName;
    }

    public void setName(String name) {
        this.mouldName = name == null ? null : name.trim();
    }

    public String getPath() {
        return mouldPath;
    }

    public void setPath(String path) {
        this.mouldPath = path == null ? null : path.trim();
    }

    public String getDescribe() {
        return mouldDescribe;
    }

    public void setDescribe(String describe) {
        this.mouldDescribe = describe == null ? null : describe.trim();
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }

    public Integer getShow() {
        return mouldShow;
    }

    public void setShow(Integer show) {
        this.mouldShow = show;
    }

    public Integer getStatus() {
        return mouldStatus;
    }

    public void setStatus(Integer status) {
        this.mouldStatus = status;
    }

    public String getWebFile() {
        return webFile;
    }

    public void setWebFile(String webFile) {
        this.webFile = webFile;
    }
}