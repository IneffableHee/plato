package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.user.model.Permission;
import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.user.service.IRoleService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/home")
public class HomePageController {

    @Autowired
    IRoleService roleService;

    @Autowired
    IPermissionService permissionService;

    @Autowired
    IRolePermissionService rolePermissionService;

    @RequestMapping("/menu")
    @RequiresAuthentication
    public PlatoBasicResult getMenu(){
        List<Permission> menus = new ArrayList<>();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(user.getUserName().equals("qnyj_001")||user.getUserName().equals("qnyj_003")||user.getUserName().equals("qnyj_002")){
            List<Permission> permissions = permissionService.getAll();
            for(Permission permission : permissions){
                if(permission.getPermissionType() == 2)
                    menus.add(permission);
            }
            return PlatoResult.successResult(menus);
        }
        List<Role> roleList = roleService.getByUserId(user.getId());
        if(CollectionUtils.isNotEmpty(roleList)){
            for(Role role : roleList){
                log.info("------Role:"+role.getRoleName()+","+role.getRoleId());
                List<Integer> permissionList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
                if(permissionList!=null&&permissionList.size()>0){
                    for (Integer pid : permissionList){
                        log.info("pid:"+pid);
                        if(pid!=null){
                            Permission permission = permissionService.getById(pid);
                            if(permission.getPermissionType() == 2){
                                menus.add(permission);
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(menus, new Comparator<Permission>(){
            public int compare(Permission p1, Permission p2) {
                if(p1.getParentId() > p2.getParentId()){
                    return 1;
                }
                if(p1.getParentId() == p2.getParentId()){
                    return 0;
                }
                return -1;
            }
        });

        for(Permission menu:menus){
            log.info(menu.getPermissionUrl()+","+menu.getPermissionName());
        }

        if(CollectionUtils.isNotEmpty(menus)){
            return PlatoResult.successResult(menus);
        }else{
            return PlatoResult.failResult("用户没有菜单权限！");
        }
    }
}
