package com.cbrc.plato.user.model;

import java.util.Date;
import java.util.List;

public class WebUser{
    private Integer id;
    private String userName;
    private String userPhone;
    private String userRealName;
    private Integer userStatus;
    private String userLevel;
    private Integer userDepId;
    private Integer userRoleId;
    private Date createTime;
    private Date lastLoginTime;
    private Date userUpdatetime;
    private List<Integer> roleList;
    private Integer createUser;
    private String token;
    private String param1;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    private String param2;
    public WebUser(){

    }

    public WebUser(User user){
        this.id = user.getId();
        this.userName = user.getUserName();
        this.userPhone = user.getUserPhone();
        this.userRealName = user.getUserRealName();
        this.userStatus = user.getUserStatus();
        this.userLevel = user.getUserLevel();
        this.userDepId = user.getUserDepId();
        this.userRoleId = user.getUserRoleId();
        this.createTime = user.getCreateTime();
        this.lastLoginTime = user.getLastLoginTime();
        this.userUpdatetime = user.getUserUpdatetime();
        this.roleList = user.getRoleList();
        this.createUser = user.getCreateUser();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getUserDepId() {
        return userDepId;
    }

    public void setUserDepId(Integer userDepId) {
        this.userDepId = userDepId;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUserUpdatetime() {
        return userUpdatetime;
    }

    public void setUserUpdatetime(Date userUpdatetime) {
        this.userUpdatetime = userUpdatetime;
    }

    public List<Integer> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Integer> roleList) {
        this.roleList = roleList;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }
}
