package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.RolePermission;

import java.util.List;

public interface IRolePermissionService {
    int insertSelective(RolePermission role);

    List<Integer> getPermisionsByRoleId(int rid);

    int setRolePermisionsByRoleId(int rid,List<Integer> permissions);

    int clearPermissionsByRoleId(int rid);

    int deleteByRoleId(int roleId);
}
