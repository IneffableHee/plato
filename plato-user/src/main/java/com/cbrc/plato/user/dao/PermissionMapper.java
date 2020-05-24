package com.cbrc.plato.user.dao;

import com.cbrc.plato.user.model.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PermissionMapper {
    int deleteByPrimaryKey(Integer permissionId);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer permissionId);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);

    ArrayList<Permission> selectAllByRoleId(Integer roleId);

    List<Permission> selectAllByRoleIdAndName(@Param("roleId") int roleId ,@Param("permissionName") String permissionName);

    Permission selectByName(String permissionByName);

    List<Permission> selectAll();
}
