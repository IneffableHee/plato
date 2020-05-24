package com.cbrc.plato.core.basic.model;

import java.util.Date;

public class ReportGeneration {
    private Integer id;

    private String reportTime;

    private Integer generationStatus;

    private Date generationTime;

    private Integer generationUser;

    private Date updateTime;

    private Integer lastUpdateUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime == null ? null : reportTime.trim();
    }

    public Integer getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(Integer generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Date getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Date generationTime) {
        this.generationTime = generationTime;
    }

    public Integer getGenerationUser() {
        return generationUser;
    }

    public void setGenerationUser(Integer generationUser) {
        this.generationUser = generationUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(Integer lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }
}