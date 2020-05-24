/**
 * @Auther: heyong
 * @Date: 2019/3/19 09:30
 * @Description:
 */
package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.Permission;

import java.util.ArrayList;
import java.util.List;

public interface IPermissionService {
    List<Permission> getByRoleId(int rid);
    List<Permission> getMenuByRoleId(int rid);
    List<Permission> getAll();
    ArrayList<Permission> selectAllByRoleId(int currentUserId);
    List<Permission> selectAllByRoleIdAndName(int roleId , String permissionName);
    List<Integer> selectUserBankGroupPermission(int userId);
    Permission getById(int pid);
    Permission getByName(String permissionByName);
    int update(Permission permission);
    int intsertPermission(Permission permission);
    int deleteById(Integer id);
}
