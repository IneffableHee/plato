package com.cbrc.plato.core.basic.model;

import java.io.Serializable;

/**
 * qnyj_data
 * @author 
 */
public class Data implements Serializable {
    /**
     * 主键，自增
     */
    private Integer dataId;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据值
     */
    private String dataValue;

    /**
     * 数据期次
     */
    private String dataTime;

    /**
     * 数据类型（月报/季报/年报）
     */
    private String dataType;

    /**
     * 部门id
     */
    private Integer depId;

    /**
     * 部门名称
     */
    private String depName;

    /**
     * 唯一编码标识
     */
    private String onlyCode;

    /**
     * 后置条件父节点
     */
    private String parent;

    /**
     * 所属银行id
     */
    private Integer bankId;

    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 对应当期表中唯一id
     */
    private Integer dataInfoId;

    /**
     * 数据表名
     */
    private String excelName;

    /**
     * 所用数据唯一标识码
     */
    private String excelCode;

    /**
     * 对应信息表id
     */
    private Long excelSourceId;

    /**
     * 备用参数1
     */
    private String param1;

    /**
     * 备用参数2
     */
    private String param2;

    private String dataRule;

    private static final long serialVersionUID = 1L;

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
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

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Integer dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public String getExcelCode() {
        return excelCode;
    }

    public void setExcelCode(String excelCode) {
        this.excelCode = excelCode;
    }

    public Long getExcelSourceId() {
        return excelSourceId;
    }

    public void setExcelSourceId(Long excelSourceId) {
        this.excelSourceId = excelSourceId;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
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
        Data other = (Data) that;
        return (this.getDataId() == null ? other.getDataId() == null : this.getDataId().equals(other.getDataId()))
            && (this.getDataName() == null ? other.getDataName() == null : this.getDataName().equals(other.getDataName()))
            && (this.getDataValue() == null ? other.getDataValue() == null : this.getDataValue().equals(other.getDataValue()))
            && (this.getDataTime() == null ? other.getDataTime() == null : this.getDataTime().equals(other.getDataTime()))
            && (this.getDataType() == null ? other.getDataType() == null : this.getDataType().equals(other.getDataType()))
            && (this.getDepId() == null ? other.getDepId() == null : this.getDepId().equals(other.getDepId()))
            && (this.getDepName() == null ? other.getDepName() == null : this.getDepName().equals(other.getDepName()))
            && (this.getParent() == null ? other.getParent() == null : this.getParent().equals(other.getParent()))
            && (this.getBankId() == null ? other.getBankId() == null : this.getBankId().equals(other.getBankId()))
            && (this.getBankName() == null ? other.getBankName() == null : this.getBankName().equals(other.getBankName()))
            && (this.getDataInfoId() == null ? other.getDataInfoId() == null : this.getDataInfoId().equals(other.getDataInfoId()))
            && (this.getExcelName() == null ? other.getExcelName() == null : this.getExcelName().equals(other.getExcelName()))
            && (this.getExcelCode() == null ? other.getExcelCode() == null : this.getExcelCode().equals(other.getExcelCode()))
            && (this.getExcelSourceId() == null ? other.getExcelSourceId() == null : this.getExcelSourceId().equals(other.getExcelSourceId()))
            && (this.getParam1() == null ? other.getParam1() == null : this.getParam1().equals(other.getParam1()))
            && (this.getParam2() == null ? other.getParam2() == null : this.getParam2().equals(other.getParam2()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDataId() == null) ? 0 : getDataId().hashCode());
        result = prime * result + ((getDataName() == null) ? 0 : getDataName().hashCode());
        result = prime * result + ((getDataValue() == null) ? 0 : getDataValue().hashCode());
        result = prime * result + ((getDataTime() == null) ? 0 : getDataTime().hashCode());
        result = prime * result + ((getDataType() == null) ? 0 : getDataType().hashCode());
        result = prime * result + ((getDepId() == null) ? 0 : getDepId().hashCode());
        result = prime * result + ((getDepName() == null) ? 0 : getDepName().hashCode());
        result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
        result = prime * result + ((getBankId() == null) ? 0 : getBankId().hashCode());
        result = prime * result + ((getBankName() == null) ? 0 : getBankName().hashCode());
        result = prime * result + ((getDataInfoId() == null) ? 0 : getDataInfoId().hashCode());
        result = prime * result + ((getExcelName() == null) ? 0 : getExcelName().hashCode());
        result = prime * result + ((getExcelCode() == null) ? 0 : getExcelCode().hashCode());
        result = prime * result + ((getExcelSourceId() == null) ? 0 : getExcelSourceId().hashCode());
        result = prime * result + ((getParam1() == null) ? 0 : getParam1().hashCode());
        result = prime * result + ((getParam2() == null) ? 0 : getParam2().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", dataId=").append(dataId);
        sb.append(", dataName=").append(dataName);
        sb.append(", dataValue=").append(dataValue);
        sb.append(", dataTime=").append(dataTime);
        sb.append(", dataType=").append(dataType);
        sb.append(", depId=").append(depId);
        sb.append(", depName=").append(depName);
        sb.append(", parent=").append(parent);
        sb.append(", bankId=").append(bankId);
        sb.append(", bankName=").append(bankName);
        sb.append(", dataInfoId=").append(dataInfoId);
        sb.append(", excelName=").append(excelName);
        sb.append(", excelCode=").append(excelCode);
        sb.append(", excelSourceId=").append(excelSourceId);
        sb.append(", param1=").append(param1);
        sb.append(", param2=").append(param2);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public String getDataRule() {
        return dataRule;
    }

    public void setDataRule(String dataRule) {
        this.dataRule = dataRule;
    }
}