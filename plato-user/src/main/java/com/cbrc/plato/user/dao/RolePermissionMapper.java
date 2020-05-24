package com.cbrc.plato.user.dao;

import com.cbrc.plato.user.model.RolePermission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionMapper {
    int deleteByPrimaryKey(Integer rpId);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    RolePermission selectByPrimaryKey(Integer rpId);

    RolePermission selectByRoleIdAndPermissionId(Integer rid,Integer pid);

    int updateByPrimaryKeySelective(RolePermission record);

    int updateByPrimaryKey(RolePermission record);

    List<Integer> selectPermisionsByRoleId(int rid);

    int deleteByRoleId(int roleId);

}
