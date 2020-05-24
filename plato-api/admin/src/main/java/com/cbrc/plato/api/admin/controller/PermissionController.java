package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.user.model.Permission;
import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.model.RolePermission;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.user.service.IRoleService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    IRoleService roleService;
    @Autowired
    IPermissionService permissionService;
    @Autowired
    IRolePermissionService rolePermissionService;

    @RequestMapping("/create")
    @RequiresPermissions("permission:create")
    public PlatoBasicResult create(Permission permission){
        if( null != permissionService.getByName (permission.getPermissionName ())){
            return PlatoResult.failResult("该权限已有，请另取名！");
        }
        Timestamp time = DateUtils.getCurrentTimestamp();
        permission.setCreateTime(time);
        permissionService.intsertPermission (permission);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
        //  用户创建权限后，根据当前的用户归类到role_permissionbiao下
        RolePermission rolePermission = new RolePermission ();
        int permissionId = permissionService.getByName (permission.getPermissionName ()).getPermissionId ();
        rolePermission.setPermissionId (permissionId);
        rolePermission.setCreateTime (time);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        rolePermission.setRoleId (currentUser.getUserRoleId ());
        rolePermissionService.insertSelective (rolePermission);
        return PlatoResult.successResult("新增权限成功！");
    }

    @RequestMapping("/delete")
    @RequiresPermissions("permission:delete")
    public PlatoBasicResult delete(Integer id){
        if (null == permissionService.getById (id)){
            return  PlatoResult.failResult("该权限不存在！");
        }
        permissionService.deleteById (id);
        return PlatoResult.successResult();
    }

    @RequestMapping("/edit")
    @RequiresPermissions("permission:update")
    public PlatoBasicResult edit(Permission permission){
        if (null == permissionService.getById (permission.getPermissionId ())){
            return  PlatoResult.failResult("该权限不存在！");
        }
        Timestamp time = DateUtils.getCurrentTimestamp();
        permission.setUpdateTime (time);
        permissionService.update (permission);
        return PlatoResult.successResult("权限修改成功！");
    }

    @RequestMapping("/list")
    @RequiresPermissions("permission:list")
    public PlatoBasicResult list(Integer  pageNo,Integer  pageSize , String permissionName){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        PageHelper.startPage(pageNo,pageSize);
        //  根据当前的用户查询相应的权限
        List<Permission> IPermissionList  = permissionService.selectAllByRoleIdAndName(currentUser.getUserRoleId (),permissionName);
        PageInfo<Permission> permissionPageInfo = new PageInfo<>(IPermissionList);
        return PlatoResult.successResult(permissionPageInfo);
    }

    @RequestMapping("/listByRoleId")
    @RequiresPermissions("permission:list")
    public PlatoBasicResult listByRoleId(int rid){
        List<Permission> userPermissions = new ArrayList<>();
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser.getUserName().equals("qnyj_001")||currentUser.getUserName().equals("qnyj_003")||currentUser.getUserName().equals("qnyj_002")){

            List<Integer> rolePermissions = new ArrayList<>();
            Role role = roleService.getByRoleId(rid);
            if(role!=null){
                List<Integer> permissionList = new ArrayList<>();
                permissionList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
                if(CollectionUtils.isNotEmpty(permissionList)){
                    for (Integer pid : permissionList){
                        Permission permission = permissionService.getById(pid);
                        if(permission!=null){
                            rolePermissions.add(permission.getPermissionId());
                        }
                    }
                }
            }

            JSON json = new JSONObject();
            ((JSONObject) json).put("userPermissions", permissionService.getAll());
            ((JSONObject) json).put("rolePermissions", rolePermissions);
            return PlatoResult.successResult(json);
        }
        List<Role> roleList = roleService.getByUserId(currentUser.getId());
        if(CollectionUtils.isNotEmpty(roleList)){
            for(Role role : roleList){
                log.info("------Role:"+role.getRoleName()+","+role.getRoleId());
                List<Integer> permissionList = new ArrayList<>();
                permissionList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
                if(CollectionUtils.isNotEmpty(permissionList)){
                    for (Integer pid : permissionList){
                        Permission permission = permissionService.getById(pid);
                        if(permission!=null){
                            userPermissions.add(permission);
                        }
                    }
                }
            }
        }

        List<Integer> rolePermissions = new ArrayList<>();
        Role role = roleService.getByRoleId(rid);
        if(role!=null){
            List<Integer> permissionList = new ArrayList<>();
            permissionList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
            if(CollectionUtils.isNotEmpty(permissionList)){
                for (Integer pid : permissionList){
                    Permission permission = permissionService.getById(pid);
                    if(permission!=null){
                        rolePermissions.add(permission.getPermissionId());
                    }
                }
            }
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("userPermissions", userPermissions);
        ((JSONObject) json).put("rolePermissions", rolePermissions);
        return PlatoResult.successResult(json);
    }
}
