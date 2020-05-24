package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.model.UserRole;
import com.cbrc.plato.user.model.WebUser;
import com.cbrc.plato.user.service.IUserRoleService;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.user.service.IWebUserService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IUserService userService;

    @Autowired
    IWebUserService webUserService;

    @Autowired
    IUserRoleService userRoleService;


    @RequestMapping("/create")
    @RequiresPermissions("admin:create")
    public PlatoBasicResult create( User user){
        if( "admin" == user.getUserName()){
            return PlatoResult.failResult("超级管理员不可创建！");
        }

        if(null != userService.getByName(user.getUserName())){
            return PlatoResult.failResult("用户名已存在，请核实后进行新增操作！");
        }
        Timestamp time = DateUtils.getCurrentTimestamp();
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        user.setCreateUser(currentUser.getId());
        user.setCreateTime(time);
        user.setUserUpdatetime(time);
        user.setIsAdmin (1);
        user.setUserPwd ("123456");
        user.setSalt(new SecureRandomNumberGenerator().nextBytes().toHex());
        userService.create (user);
       /* try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }*/
        if(null != user.getUserRoleId ()) {
            UserRole userRole = new UserRole ();
            userRole.setRoleId (user.getUserRoleId ());
            log.info ("getUserName"+user.getUserName ());
            WebUser userSelectId = webUserService.getByName (user.getUserName ());
            userRole.setUserId (userSelectId.getId ());
            userRole.setRoleId (user.getUserRoleId ());
            Timestamp timestamp = DateUtils.getCurrentTimestamp();
            userRole.setCreateTime (timestamp);
            userRoleService.insertSelective (userRole);
        }
        return PlatoResult.successResult("用户添加成功！");
    }

    @RequestMapping("/delete")
    @RequiresPermissions("admin:delete")
    public PlatoBasicResult delete(Integer id){
        if("admin" == userService.getById(id).getUserName()){
            return PlatoResult.failResult("超级管理员不可删除！");
        }
        if (null == userService.getById(id)){
            return PlatoResult.failResult("用户不存在！");
        }

        if(null != userService.getById (id).getUserRoleId ()){
            userRoleService.deleteById (id);
        }
        userService.deleteById(id);
        return PlatoResult.successResult("删除成功！");
    }


    @RequestMapping("/batchremove")
    //@RequiresPermissions("admin:batchremove")
    public PlatoBasicResult batchremove(String ids){
        List<String> result = Arrays.asList(ids.split(","));
        for(int t=0;t<result.size();t++){
            if (null == userService.getById(Integer.parseInt(result.get(t)))){
                return PlatoResult.failResult("用户不存在！");
            }
            if("admin" == userService.getById(Integer.parseInt(result.get(t))).getUserName()){
                return PlatoResult.failResult("超级管理员不可删除！");
            }
            if(null != userService.getById (Integer.parseInt(result.get(t))).getUserRoleId ()){
                userRoleService.deleteById (Integer.parseInt(result.get(t)));
            }
        }
        userService.deleteByPrimaryKeyAll(ids);
        return PlatoResult.successResult("批量删除成功！");
    }

    @RequestMapping("/update")
    @RequiresPermissions("admin:update")
    public PlatoBasicResult update(User user){
        if("admin" == user.getUserName()){
            return PlatoResult.failResult("超级管理员不可修改！");
        }
        if(userService.getById(user.getId()) == null){
            return PlatoResult.failResult("用户信息不存在,请核实后再修改！");
        }
        user.setUserUpdatetime(DateUtils.getCurrentTimestamp());
        if(user.getUserRoleId ()==1){
            user.setIsAdmin (1);
        }else{
            user.setIsAdmin (0);
        }
        userService.update(user);

        UserRole  userRole = userRoleService.selectRoleUserByUserid (user.getId ());
        if(null != user.getUserRoleId () && null != userRole){
            if(userRole.getRoleId () != user.getUserRoleId ()){
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = DateUtils.getCurrentTimestamp();
                String param1 = sdf.format (timestamp);
                userRole.setRoleId (user.getUserRoleId ());
                userRole.setParam1 (param1);
                userRoleService.updateUserRole (userRole);
            }
        }else if(null == user.getUserRoleId () && null != userRole){
            userRoleService.deleteById (user.getId ());
        }else if(null != user.getUserRoleId () && null == userRole){
            UserRole userRole1 =new UserRole ();
            Timestamp timestamp = DateUtils.getCurrentTimestamp();
            userRole1.setRoleId (user.getUserRoleId ());
            userRole1.setUserId (user.getId ());
            userRole1.setCreateTime (timestamp);
            userRoleService.insertSelective (userRole1);
        }
        return PlatoResult.successResult("用户信息修改成功！");
    }

    @RequestMapping("/listpage")
    @RequiresPermissions("admin:list")
    public PlatoBasicResult listpage(Integer  pageNo,Integer  pageSize , String userRealName){
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<WebUser> adminList = webUserService.selectAdminAllByUserRealName(userRealName);
        PageInfo<WebUser> userPageInfo = new PageInfo<>(adminList);
        return PlatoResult.successResult(userPageInfo);
    }

    @RequestMapping("/list")
    @RequiresPermissions("admin:list")
    public PlatoBasicResult list(){
        ArrayList<WebUser> adminList = webUserService.getAdminList();
        return PlatoResult.successResult(adminList);
    }

}
