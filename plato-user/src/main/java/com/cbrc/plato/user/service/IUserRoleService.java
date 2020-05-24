package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.UserRole;

public interface IUserRoleService {

    public    int insertSelective(UserRole  userRole);

    public    int  deleteById(int userId);

    public UserRole selectRoleUserByUserid(int userId);

    public   int    updateUserRole (UserRole userRole);
}
