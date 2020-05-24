package com.cbrc.plato.user.dao;

import com.cbrc.plato.user.model.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    ArrayList<Role> selectAll();

    List<Role> selectAllByRoleName(@Param("roleName") String roleName);

    Role selectByName(String departmentByName);

    int insertRole(Role role);
}
