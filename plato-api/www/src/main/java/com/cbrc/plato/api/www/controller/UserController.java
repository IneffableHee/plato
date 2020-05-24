/**
 * @Auther: heyong
 * @Date: 2019/3/14 09:35
 * @Description:
 */
package com.cbrc.plato.api.www.controller;

import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    /*修改个人信息*/
    @RequestMapping("/modify")
    public PlatoBasicResult modify(User user){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(user.getId()!=currentUser.getId()){
            return PlatoResult.failResult("修改个人信息失败！");
        }
        userService.update(user);
        return PlatoResult.successResult();
    }

}
