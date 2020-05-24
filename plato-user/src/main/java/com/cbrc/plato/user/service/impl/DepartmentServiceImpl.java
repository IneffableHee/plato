package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.DepartmentMapper;
import com.cbrc.plato.user.model.Department;
import com.cbrc.plato.user.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    DepartmentMapper  departmentMapper;
//    @Autowired
//    Idepartmentservice departmentservice ;
    @Override
    public List<Department> getByUserId(int id) {
        return null;
    }

    @Override
    public Department getBydepartmentId(Integer id) {
        return this.departmentMapper.selectByPrimaryKey (id);
    }

    @Override
    public Department getByName(String departmentByName) {
        return this.departmentMapper.selectByName (departmentByName);
    }

    @Override
    public ArrayList<Department> getDepartmentList( ) {
        return this.departmentMapper.selectAll ();
    }

    @Override
    public int intsertDepartment(Department department) {
        return this.departmentMapper.insertSelective (department);
    }

    @Override
    public int deleteById(Integer id) {
        return this.departmentMapper.deleteByPrimaryKey (id);
    }

    @Override
    public int update(Department department) {
        return this.departmentMapper.updateByPrimaryKeySelective (department);
    }

    @Override
    public List<Department> getChildListById(Integer id) {
        return this.departmentMapper.selectChildListById(id);
    }
}
