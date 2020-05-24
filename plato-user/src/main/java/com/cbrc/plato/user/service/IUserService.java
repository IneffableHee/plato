/**
 * @Auther: heyong
 * @Date: 2019/3/13 16:28
 * @Description:
 */
package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.model.WebUser;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface IUserService {

    public User getByName(String uName);

    public User getById(Integer id);

    public int create(User user);

    public int deleteById(Integer id);

    public int deleteByPrimaryKeyAll(String ids);

    public int update(User user);

    public ArrayList<User>  getUserList();

    List<WebUser> selectAllByUserRealName(String UserName);

    WebUser selectUserInfoById(Integer userId);
}
