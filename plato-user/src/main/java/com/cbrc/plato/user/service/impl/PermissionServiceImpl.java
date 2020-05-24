/**
 * @Auther: heyong
 * @Date: 2019/3/21 09:29
 * @Description:
 */
package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.PermissionMapper;
import com.cbrc.plato.user.model.Permission;
import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.user.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    IRoleService roleService;

    @Autowired
    IPermissionService permissionService;

    @Autowired
    IRolePermissionService rolePermissionService;

    @Override
    public List<Permission> getByRoleId(int rid) {
        System.out.println(rid+"获取当前登录用户的roleID");
        Role role = roleService.getByRoleId(rid);
        List<Integer> permissionIdList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
        if(CollectionUtils.isNotEmpty(permissionIdList)){
            List<Permission> permissionList=new ArrayList<>();
            for(Integer pid:permissionIdList){
                Permission permission = this.permissionMapper.selectByPrimaryKey(pid);
                if(permission!=null)
                    permissionList.add(permission);
            }
            return permissionList;
        }
        return null;
    }

    @Override
    public List<Permission> getMenuByRoleId(int rid) {
        System.out.println(rid+"getMenuByRoleId");
        Role role = roleService.getByRoleId(rid);
        List<Integer> permissionIdList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
        if(CollectionUtils.isNotEmpty(permissionIdList)){
            List<Permission> permissionList=new ArrayList<>();
            for(Integer pid:permissionIdList){
                Permission permission = this.permissionMapper.selectByPrimaryKey(pid);
                if(permission!=null&&permission.getPermissionType()==2)
                    permissionList.add(permission);
            }
            return permissionList;
        }
        return null;
    }

    @Override
    public List<Permission> getAll() {return this.permissionMapper.selectAll();}
    @Override
    public ArrayList<Permission> selectAllByRoleId(int roleId){return this.permissionMapper.selectAllByRoleId (roleId);}

    @Override
    public List<Permission> selectAllByRoleIdAndName(int roleId, String permissionName) {
        return this.permissionMapper.selectAllByRoleIdAndName(roleId,permissionName);
    }

    @Override
    public List<Integer> selectUserBankGroupPermission(int userId) {
        List<Integer> permissions = new ArrayList<>();
        List<Role> roleList = roleService.getByUserId(userId);
        if(CollectionUtils.isNotEmpty(roleList)){
            for(Role role : roleList){
                log.info("------Role:"+role.getRoleName());
                List<Integer> pidList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
                if(CollectionUtils.isNotEmpty(pidList)){
                    for (Integer pid : pidList){
                        Permission permission = permissionService.getById(pid);
                        if(permission!= null && StringUtils.isNoneBlank(permission.getPermissionUrl())){
                            log.info("---Permissiom:"+permission.getPermissionUrl());
                            if(permission.getPermissionType() == 3)
                                permissions.add(permission.getPermissionId());
                        }
                    }
                }
            }
        }
        return permissions;
    }

    @Override
    public Permission getById(int pid) {
        return this.permissionMapper.selectByPrimaryKey(pid);
    }
    @Override
    public Permission getByName(String permissionByName){return this.permissionMapper.selectByName (permissionByName);};
    @Override
    public int update(Permission permission){return permissionMapper.updateByPrimaryKeySelective (permission);};
    @Override
    public int intsertPermission(Permission permission){return permissionMapper.insertSelective (permission);};
    @Override
    public int deleteById(Integer id){return permissionMapper.deleteByPrimaryKey (id);};
}
