package com.cbrc.plato.user.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class Department implements Serializable {
    private Integer departmentId;

    private String departmentName;

    private String departmentCode;

    private String departmentShortName;

    private Integer parentId;

    private String departmentDescription;

    private String departmentMobile;

    private Integer departmentOrder;

    private Integer departmentStatus;

    private String param1;

//    public Department(Integer departmentId, String departmentName, String departmentCode, String departmentShortName, Integer parentId, String departmentDescription, String departmentMobile, Integer departmentOrder, Integer departmentStatus, String param1) {
//        this.departmentId = departmentId;
//        this.departmentName = departmentName;
//        this.departmentCode = departmentCode;
//        this.departmentShortName = departmentShortName;
//        this.parentId = parentId;
//        this.departmentDescription = departmentDescription;
//        this.departmentMobile = departmentMobile;
//        this.departmentOrder = departmentOrder;
//        this.departmentStatus = departmentStatus;
//        this.param1 = param1;
//    }
    public static TreeItem deptNode(Department dept) {
        TreeItem treeItem = new TreeItem();
//        treeItem.setId(dept.getId());
//        treeItem.setName(dept.getName());
//        treeItem.setParentId(dept.getParentId());
        treeItem.setId (dept.getDepartmentId ());
        treeItem.setName (dept.departmentShortName);
        treeItem.setParentId (dept.getParentId ());
        treeItem.setDepartmentId (dept.getDepartmentId ());

        treeItem.setDepartmentName (dept.departmentName);
        treeItem.setDepartmentCode (dept.departmentCode);
        treeItem.setDepartmentShortName ((dept.departmentShortName));
        treeItem.setDepartmentDescription (dept.departmentDescription);
        treeItem.setDepartmentMobile (dept.departmentMobile);
        treeItem.setDepartmentStatus (dept.departmentStatus);
        return treeItem;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName == null ? null : departmentName.trim();
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode == null ? null : departmentCode.trim();
    }

    public String getDepartmentShortName() {
        return departmentShortName;
    }

    public void setDepartmentShortName(String departmentShortName) {
        this.departmentShortName = departmentShortName == null ? null : departmentShortName.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getDepartmentDescription() {
        return departmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription == null ? null : departmentDescription.trim();
    }

    public String getDepartmentMobile() {
        return departmentMobile;
    }

    public void setDepartmentMobile(String departmentMobile) {
        this.departmentMobile = departmentMobile == null ? null : departmentMobile.trim();
    }

    public Integer getDepartmentOrder() {
        return departmentOrder;
    }

    public void setDepartmentOrder(Integer departmentOrder) {
        this.departmentOrder = departmentOrder;
    }

    public Integer getDepartmentStatus() {
        return departmentStatus;
    }

    public void setDepartmentStatus(Integer departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1 == null ? null : param1.trim();
    }
}
