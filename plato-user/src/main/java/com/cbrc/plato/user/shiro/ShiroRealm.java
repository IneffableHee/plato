package com.cbrc.plato.user.shiro;

import com.cbrc.plato.user.model.Permission;
import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.user.service.IRoleService;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.util.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IRolePermissionService rolePermissionService;


    private SessionManager sessionManager;
    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     *
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        log.info("---------------- 执行 Shiro 凭证认证 ----------------------");
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String name = token.getUsername();
        // 从数据库获取对应用户名密码的用户
        User user = userService.getByName(name);
        if (user != null) {
            //用户已删除
            if(user.getUserStatus()==2)
                throw new UnknownAccountException();
            // 用户为禁用状态
            if (user.getUserStatus()==0) {
                throw new DisabledAccountException();
            }
            user.setLastLoginTime(DateUtils.currentTimestamp());
            userService.update(user);

            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user, //用户
                    user.getUserPwd(), //密码
//                    salt=username+salt
                    ByteSource.Util.bytes(user.getUserName()+user.getSalt()),
                    getName()  //realm name
            );

            //单用户登录
            //处理session
            DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
            DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
            //获取当前已登录的用户session列表
            Collection<Session> sessions = sessionManager.getSessionDAO().getActiveSessions();
            User temp;
            for(Session session : sessions){
//                log.info("》》》》sessionId:"+session.getId().toString());
                //清除该用户以前登录时保存的session，强制退出
                Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                if (attribute == null) {
                    continue;
                }

                temp = (User) ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
//                log.info("》》》》User:"+temp.getUserName());
                if(name.equals(temp.getUserName())) {
                    if(session.getAttribute("kickout") == null){
                        session.setAttribute("kickout","true");
                        sessionManager.getSessionDAO().update(session);
                        if(session.getAttribute("kickout")!=null)
                            log.info("set kickout! sessionId:"+session.getId().toString());
//                    sessionManager.getSessionDAO().delete(session);
                    }
                }
            }
            log.info("---------------- Shiro 凭证认证成功 ----------------------");
            return authenticationInfo;
        }
        throw new UnknownAccountException();
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("---------------- 执行 Shiro 权限获取 ---------------------");
        Object principal = principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (principal instanceof User) {
            User userLogin = (User) principal;
            log.info("---------------- User: {}---------------------",userLogin.getUserName());
            if(userLogin != null){
                if(userLogin.getUserName().equals("qnyj_001")||userLogin.getUserName().equals("qnyj_003")||userLogin.getUserName().equals("qnyj_002")){
                    List<Permission> permissions = permissionService.getAll();
                    for(Permission permission : permissions){
                        if(permission.getPermissionType() == 1)
                            info.addStringPermission(permission.getPermissionUrl());
                    }
                }else{
                    List<Role> roleList = roleService.getByUserId(userLogin.getId());
                    if(CollectionUtils.isNotEmpty(roleList)){
                        for(Role role : roleList){
                            info.addRole(role.getRoleName());
                            log.info("------Role:"+role.getRoleName());
                            List<Integer> pidList = rolePermissionService.getPermisionsByRoleId(role.getRoleId());
                            if(CollectionUtils.isNotEmpty(pidList)){
                                for (Integer pid : pidList){
                                    Permission permission = permissionService.getById(pid);
                                    if(permission!= null && StringUtils.isNoneBlank(permission.getPermissionUrl())){
                                        log.info("---Permissiom:"+permission.getPermissionUrl());
                                        if(permission.getPermissionType() == 1)
                                            info.addStringPermission(permission.getPermissionUrl());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(CollectionUtils.isNotEmpty(info.getStringPermissions())){
            log.info("---------------- 获取到以下权限 ----------------");
            log.info(info.getStringPermissions().toString());
            log.info("---------------- Shiro 权限获取成功 ----------------------");
        }

        return info;
    }
}
