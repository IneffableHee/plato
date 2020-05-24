package com.cbrc.plato.user.dao;

import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.model.WebUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int deleteByPrimaryKeyAll(@Param("ids")String ids);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    WebUser selectUserInfoById(Integer userId);

    WebUser selectWebUserByUserName(String userName);

    User selectByUserName(String userName);

    int update(User record);

    int updateByPrimaryKey(User record);

    ArrayList<User> selectAll();

    ArrayList<WebUser> selectAdminWebUserAll();

    List<WebUser> selectAdminAllByUserRealName(@Param("userRealName") String userRealName);

    List<WebUser> selectAllByUserRealName(@Param("userRealName") String userRealName);

    ArrayList<WebUser> selectWebUserAll();
}
