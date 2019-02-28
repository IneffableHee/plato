package com.cbrc.plato.core.east.service.impl;


import com.cbrc.plato.core.east.dao.EastUserMapper;
import com.cbrc.plato.core.east.model.EastUser;
import com.cbrc.plato.core.east.service.IEastUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EastUserService implements IEastUserService {

    @Autowired
    EastUserMapper userMapper;

    @Override
    public String sayHello(int uid) {
        EastUser user = userMapper.selectByPrimaryKey(uid);
        if(user!=null)
            return "Hello!,I'm "+user.getUserName()+",I'm "+user.getUserAge()+" years old";
        else
            return "用户不存在！";
    }
}
