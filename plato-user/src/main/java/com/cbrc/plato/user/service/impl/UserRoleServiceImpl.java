package com.cbrc.plato.user.service.impl;


import com.cbrc.plato.user.dao.UserRoleMapper;
import com.cbrc.plato.user.model.UserRole;
import com.cbrc.plato.user.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements IUserRoleService {


    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    public int insertSelective(UserRole role) {
        return this.userRoleMapper.insertSelective (role);
    }

    @Override
    public int deleteById(int userId) {
        return this.userRoleMapper.deleteByPrimaryKey (userId);
    }

    @Override
    public UserRole selectRoleUserByUserid(int userId) {
        return this.userRoleMapper.selectByPrimaryKey (userId);
    }

    @Override
    public int updateUserRole(UserRole userRole) {
        return this.userRoleMapper.updateByPrimaryKeySelective (userRole);
    }

}
