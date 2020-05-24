package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.Department;

import java.util.ArrayList;
import java.util.List;


public interface IDepartmentService {
    List<Department> getByUserId(int uid);
    Department getBydepartmentId(Integer rid);
    Department getByName(String departmentByName);
    ArrayList<Department> getDepartmentList();
    int intsertDepartment(Department department);
    int deleteById(Integer id);
    int update(Department department);
    List<Department> getChildListById(Integer id);
}
