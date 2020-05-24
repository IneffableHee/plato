package com.cbrc.plato.user.model;

import lombok.Data;

import java.util.List;

@Data
public class TreeItem {
    private Integer id;

    private Integer parentId;

    private String name;

    private Integer departmentId;

    private List<TreeItem> children;


    private String departmentName;

    private String departmentCode;

    private String departmentShortName;

    private String departmentDescription;

    private String departmentMobile;

    private Integer departmentStatus;
}
