package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.model.WebUser;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    IUserService userService;

    @RequestMapping("/login")
    public PlatoBasicResult login( String username, String password){
        log.info("--------user  login-------"+username+","+password);
        if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)){
            return PlatoResult.failResult("用户名或密码为空！");
        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            User loginUser = userService.getByName(username);
            if(loginUser.getIsAdmin() != 1){
                return PlatoResult.failResult("用户名或密码错误");
            }
            if (subject.isAuthenticated()) {
                JSON json = new JSONObject();
                WebUser webUser = new WebUser(loginUser);
                webUser.setToken(subject.getSession().getId().toString());
                ((JSONObject) json).put("user", webUser);
                return PlatoResult.successResult(json);
            }else{
                return PlatoResult.failResult("登录失败");
            }
        }catch (IncorrectCredentialsException e){
            log.info(toString());
            return PlatoResult.failResult("用户名获密码错误");
        }catch (LockedAccountException e){
            log.info(e.toString());
            return PlatoResult.failResult("账户已锁定");
        }catch (DisabledAccountException e){
            log.info(e.toString());
            return PlatoResult.failResult("账户已禁用");
        } catch (UnknownAccountException e){
            log.info(e.toString());
            return PlatoResult.failResult("用户名或密码错误");
        }catch (Exception e){
            log.info("4"+e.toString());
            return PlatoResult.sysexResult();
        }
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping("/logout")
    public PlatoBasicResult logout(){
        SecurityUtils.getSubject().logout();
        return PlatoResult.successResult();
    }
}
