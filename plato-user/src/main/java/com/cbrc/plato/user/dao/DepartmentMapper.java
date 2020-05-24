package com.cbrc.plato.user.dao;

import com.cbrc.plato.user.model.Department;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface DepartmentMapper {
    int deleteByPrimaryKey(Integer departmentId);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(Integer departmentId);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);

    ArrayList<Department> selectAll();

    Department selectByName(String roleByName);

    List<Department> selectChildListById(Integer id);
}
