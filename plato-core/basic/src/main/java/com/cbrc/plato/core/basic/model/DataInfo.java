package com.cbrc.plato.core.basic.model;

import java.io.Serializable;

/**
 * qnyj_data_info
 * @author 
 */
public class DataInfo implements Serializable {
    /**
     * 主键，自增
     */
    private Integer id;

    /**
     * 银行所属组id
     */
    private Integer groupId;

    /**
     * 所属excel表唯一标识
     */
    private String excelCode;

    /**
     * 数据字段名称
     */
    private String dataName;

    /**
     * 取值单元格
     */
    private String dataSource;

    /**
     * excel表格类型（月报/季报/年报）
     */
    private String dataType;

    /**
     * 数据唯一编码
     */
    private String onlyCode;

    /**
     * 后置条件父节点
     */
    private String parent;

    /**
     * 数据状态
     */
    private Integer status;

    /**
     * 数据描述
     */
    private String description;

    /**
     * 数据所属规则id
     */
    private Integer ruleId;

    /**
     * 备用参数2
     */
    private String param2;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getExcelCode() {
        return excelCode;
    }

    public void setExcelCode(String excelCode) {
        this.excelCode = excelCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getOnlyCode() {
        return onlyCode;
    }

    public void setOnlyCode(String onlyCode) {
        this.onlyCode = onlyCode;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DataInfo other = (DataInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getExcelCode() == null ? other.getExcelCode() == null : this.getExcelCode().equals(other.getExcelCode()))
            && (this.getDataName() == null ? other.getDataName() == null : this.getDataName().equals(other.getDataName()))
            && (this.getDataSource() == null ? other.getDataSource() == null : this.getDataSource().equals(other.getDataSource()))
            && (this.getDataType() == null ? other.getDataType() == null : this.getDataType().equals(other.getDataType()))
            && (this.getParent() == null ? other.getParent() == null : this.getParent().equals(other.getParent()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getRuleId() == null ? other.getRuleId() == null : this.getRuleId().equals(other.getRuleId()))
            && (this.getParam2() == null ? other.getParam2() == null : this.getParam2().equals(other.getParam2()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getExcelCode() == null) ? 0 : getExcelCode().hashCode());
        result = prime * result + ((getDataName() == null) ? 0 : getDataName().hashCode());
        result = prime * result + ((getDataSource() == null) ? 0 : getDataSource().hashCode());
        result = prime * result + ((getDataType() == null) ? 0 : getDataType().hashCode());
        result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getRuleId() == null) ? 0 : getRuleId().hashCode());
        result = prime * result + ((getParam2() == null) ? 0 : getParam2().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", groupId=").append(groupId);
        sb.append(", excelCode=").append(excelCode);
        sb.append(", dataName=").append(dataName);
        sb.append(", dataSource=").append(dataSource);
        sb.append(", dataType=").append(dataType);
        sb.append(", parent=").append(parent);
        sb.append(", status=").append(status);
        sb.append(", description=").append(description);
        sb.append(", ruleId=").append(ruleId);
        sb.append(", param2=").append(param2);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}