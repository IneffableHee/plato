/**
 * @Auther: heyong
 * @Date: 2019/3/13 16:30
 * @Description:
 */
package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.UserMapper;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.model.WebUser;
import com.cbrc.plato.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getByName(String uName){
        return userMapper.selectByUserName (uName);
    }

    @Override
    public User getById(Integer id){ return  userMapper.selectByPrimaryKey(id); }

    @Override
    public int create(User user) {
        return this.userMapper.insertSelective(user);
    }

    @Override
    public int deleteById(Integer id) {
        return this.userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByPrimaryKeyAll(String ids) {
        return this.userMapper.deleteByPrimaryKeyAll(ids);
    }

    @Override
    public int update(User user) {
        return this.userMapper.update(user);
    }

    @Override
    public ArrayList<User> getUserList(){return this.userMapper.selectAll();}

    @Override
    public List<WebUser> selectAllByUserRealName(String userRealName) {
        return this.userMapper.selectAllByUserRealName(userRealName);
    }

    @Override
    public WebUser selectUserInfoById(Integer userId) {
        return this.userMapper.selectUserInfoById(userId);
    }

}
