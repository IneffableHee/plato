package com.cbrc.plato.api.admin.controller;


import com.cbrc.plato.user.model.*;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.user.service.IRoleService;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ArrayUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    IRoleService  roleService;
    @Autowired
    IPermissionService permissionService;
    @Autowired
    IRolePermissionService rolePermissionService;
    @Autowired
    IUserService userService;

    @RequestMapping(value = "/create", method = {RequestMethod.POST })
    @RequiresPermissions("role:create")
    public @ResponseBody PlatoBasicResult create(@RequestBody String objRole) {
        Gson g = new Gson();
        JsonObject obj = g.fromJson(objRole, JsonObject.class);
        String permiss = obj.get("permissios").toString();
        String tt = FileUtil.deleteString0(permiss,'[');
        String checkPermissionId = FileUtil.deleteString0(tt,']');
        Role role1 = new Role();
        role1.setRoleStatus(Integer.parseInt(obj.get("roleStatus").toString()));
        role1.setRoleName(FileUtil.deleteString0(obj.get("roleName").toString(),'"'));
        role1.setRoleDescription(FileUtil.deleteString0(obj.get("roleDescription").toString(),'"'));
        role1.setParam1(FileUtil.deleteString0(obj.get("param1").toString(),'"'));
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
         if(null !=checkPermissionId){
             log.info ("-----------页面传过来的字符串："+checkPermissionId);
             String[] NewcheckPermissionId =checkPermissionId.split("\\,");
             //  将页面传过来的字符串转化为数字存到整型数组
             Integer[] tempCheckPermissionId = new Integer[NewcheckPermissionId.length];
             for (int i = 0 ;i < NewcheckPermissionId.length;i++){
                 int PermissionId = Integer.parseInt(NewcheckPermissionId[i]);
                 log.info ("----"+PermissionId);
                  //  向tempCheckPermissionId 追加元素
                 tempCheckPermissionId[i] = Integer.parseInt(NewcheckPermissionId[i]);
             }
             if(null != tempCheckPermissionId){
                 List<Permission>  permissionList = permissionService.getByRoleId (currentUser.getUserRoleId ());
                 Integer []  currentUserPerssionId = new Integer[permissionList.size ()];
                 for (int i = 0; i < permissionList.size(); i++) {
                     Permission permission=permissionList.get(i);
                     currentUserPerssionId[i] = permission.getPermissionId ();
                 }
                 //   验证 再次验证创建的用户tempCheckPermissionId的权限是否为登录的用户currentUserPerssionId拥有下的权限
                 boolean isTure = ArrayUtils.containsAll (currentUserPerssionId,tempCheckPermissionId);
                 if(isTure){
                     if( null != roleService.getByName(FileUtil.deleteString0(obj.get("roleName").toString(),'"'))){
                        return PlatoResult.failResult("该权限名称已有！请另命名该权限名称！！！");
                     }
                     Timestamp time = DateUtils.getCurrentTimestamp();
                     role1.setCreateTime (time);
                     role1.setParentId (currentUser.getUserRoleId ());
                     roleService.intsertRole (role1);
                     try {
                         sleep(1000);
                     } catch (InterruptedException e) {
                         e.printStackTrace ();
                     }
                     int getRoleId = roleService.getByName (FileUtil.deleteString0(obj.get("roleName").toString(),'"')).getRoleId ();
                     for(int i = 0;i<tempCheckPermissionId.length;i++){
                           RolePermission rolePermission = new RolePermission ();
                           rolePermission.setCreateTime (time);
                           log.info ("role1.getRoleId ()："+role1.getRoleId ()+"");
                           rolePermission.setRoleId (getRoleId);
                           rolePermission.setPermissionId (tempCheckPermissionId[i]);
                           rolePermissionService.insertSelective (rolePermission);
                     }
                 }else {
                     return PlatoResult.failResult("该权限异常！！！");
                 }

             }
         }
        return PlatoResult.successResult("新增角色及权限成功！");
    }


//    @RequiresPermissions("user:create")
//    public PlatoBasicResult create(Role role){
//        if( null != roleService.getByName(role.getRoleName ())){
//            return PlatoResult.failResult("该权限名称已有！请另命名该权限名称！！！");
//        }
//        Timestamp time = DateUtils.getCurrentTimestamp();
//        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
//        role.setCreateTime (time);
//        role.setParentId (currentUser.getUserRoleId ());
//        roleService.intsertRole (role);
//        return PlatoResult.successResult();
//    }


    @RequestMapping("/delete")
    @RequiresPermissions("role:delete")
    public PlatoBasicResult delete(Integer id){
        if (null == roleService.getByRoleId (id)){
            return  PlatoResult.failResult("该角色不存在！");
        }
        rolePermissionService.deleteByRoleId(id);
        roleService.deleteById (id);
        return PlatoResult.successResult("角色及对应权限删除成功!");
    }

    @RequestMapping("/disable")
    @RequiresPermissions("role:update")
    public PlatoBasicResult disable(Integer id){
        Role role = roleService.getByRoleId (id);
        if (null == role){
            return  PlatoResult.failResult("该角色不存在！");
        }
        if(role.getRoleStatus() == 0)
            return PlatoResult.successResult();
        role.setRoleStatus(0);
        roleService.update (role);
        return PlatoResult.successResult();
    }

    @RequestMapping("/enable")
    @RequiresPermissions("role:update")
    public PlatoBasicResult enable(Integer id){
        Role role = roleService.getByRoleId (id);
        if (null == role){
            return  PlatoResult.failResult("该角色不存在！");
        }
        if(role.getRoleStatus() == 1)
            return PlatoResult.successResult();
        role.setRoleStatus(1);
        roleService.update (role);
        return PlatoResult.successResult();
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST })
    @RequiresPermissions("role:update")
     public @ResponseBody PlatoBasicResult edit(@RequestBody String objRole){
        log.info("111111111==="+objRole);
        Gson g = new Gson();
        List<Integer> pernissions = new ArrayList<Integer>();
        JsonObject obj = g.fromJson(objRole, JsonObject.class);
        String permiss = obj.get("permissios").toString();
        String tt = FileUtil.deleteString0(permiss,'[');
        String yy = FileUtil.deleteString0(tt,']');
        String[] numberArray = yy.split(",");
        for(String p :  numberArray){
            pernissions.add(Integer.parseInt(p));
        }
        Role role = roleService.getByRoleId (Integer.parseInt(obj.get("roleId").toString()));
        if (null == role){
            return  PlatoResult.failResult("该角色不存在！");
        }
        role.setRoleName(FileUtil.deleteString0(FileUtil.deleteString0(obj.get("roleName").toString(),'\\'),'"'));
        role.setRoleDescription(FileUtil.deleteString0(FileUtil.deleteString0(obj.get("roleDescription").toString(),'\\'),'"'));
        role.setParentId(Integer.parseInt(obj.get("parentId").toString()));
        role.setParam1(FileUtil.deleteString0(FileUtil.deleteString0(obj.get("param1").toString(),'\\'),'"'));
        role.setParam2(FileUtil.deleteString0(FileUtil.deleteString0(obj.get("param2").toString(),'\\'),'"'));
        if(pernissions.size()>0){
            log.info("pernissions  list "+pernissions);
            rolePermissionService.clearPermissionsByRoleId(role.getRoleId());
            HashSet h = new HashSet(pernissions);
            pernissions.clear();
            pernissions.addAll(h);
            rolePermissionService.setRolePermisionsByRoleId(role.getRoleId(),pernissions);
        }
        roleService.update (role);
        return PlatoResult.successResult("角色权限修改成功！");
    }

    @RequestMapping("/list")
    @RequiresPermissions("role:list")
    public PlatoBasicResult list(Integer  pageNo,Integer  pageSize, String roleName) throws Exception{
        PageHelper.startPage(pageNo,pageSize);
        List<Role> iRoleList = roleService.selectAllByRoleName(roleName);
        PageInfo<Role> mouldPageInfo = new PageInfo<>(iRoleList);
        return PlatoResult.successResult(mouldPageInfo);
    }

    @RequestMapping("/roleNameList")
    public PlatoBasicResult roleNameList(){
        return PlatoResult.successResult(roleService.getRoleList());
    }

    /**
     * 用户基本信息
     * @return
     */
    @RequestMapping("/handleSet")
    public @ResponseBody PlatoBasicResult handleSet(){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        return PlatoResult.successResult(currentUser);
    }

    /**
     * 用户基本信息
     * @return
     */
    @RequestMapping("/basicInfo")
    public @ResponseBody PlatoBasicResult basicInfo(){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        WebUser user = userService.selectUserInfoById(currentUser.getId());
        return PlatoResult.successResult(user);
    }

    /**
     * 用户密码修改
     * @return
     */
    @RequestMapping(value = "/editSet" , method = {RequestMethod.POST })
    public PlatoBasicResult editSet(@RequestBody String objSet){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        Gson g = new Gson();
        JsonObject obj = g.fromJson(objSet, JsonObject.class);
        //原密码校验
        //String userPwd = FileUtil.deleteString0(FileUtil.deleteString0(obj.get("userPwd").toString(),'\\'),'"');
        //新密码
        String userPwdNew = FileUtil.deleteString0(FileUtil.deleteString0(obj.get("userPwdNew").toString(),'\\'),'"');
        //确认新密码
        String userPwdNewIs = FileUtil.deleteString0(FileUtil.deleteString0(obj.get("userPwdNewIs").toString(),'\\'),'"');
        if(userPwdNew.equals(userPwdNewIs)){
            currentUser.setUserPwd(userPwdNewIs);
            userService.update(currentUser);
        }else{
            return PlatoResult.failResult("密码修改失败，两次输入的密码不匹配，请重新修改！");
        }
        //修改新密码
        return PlatoResult.successResult("密码修改成功！");
    }
}
