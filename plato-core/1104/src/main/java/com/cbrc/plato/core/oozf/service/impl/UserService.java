package com.cbrc.plato.core.oozf.service.impl;


import com.cbrc.plato.core.oozf.dao.UserMapper;
import com.cbrc.plato.core.oozf.model.User;
import com.cbrc.plato.core.oozf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public String sayHello(int uid) {
        User user = userMapper.selectByPrimaryKey(uid);
        if(user!=null)
            return "Hello!,I'm "+user.getUserName()+",I'm "+user.getUserAge()+" years old";
        else
            return "用户不存在！";
    }
}
