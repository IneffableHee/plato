package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.UserMapper;
import com.cbrc.plato.user.model.WebUser;
import com.cbrc.plato.user.service.IWebUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebUserServiceImpl implements IWebUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ArrayList<WebUser> getAdminList() {
        return userMapper.selectAdminWebUserAll();
    }

    @Override
    public ArrayList<WebUser> getUserList() {
        return userMapper.selectWebUserAll();
    }

    @Override
    public List<WebUser> selectAdminAllByUserRealName(String userRealName) {
        return userMapper.selectAdminAllByUserRealName(userRealName);
    }

    @Override
    public WebUser getByName(String webUserByName) {
        return this.userMapper.selectWebUserByUserName (webUserByName);
    }


}
