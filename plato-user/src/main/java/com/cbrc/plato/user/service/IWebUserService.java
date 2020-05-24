package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.WebUser;

import java.util.ArrayList;
import java.util.List;

public interface IWebUserService {

    public ArrayList<WebUser> getAdminList();
    public ArrayList<WebUser> getUserList();

    List<WebUser> selectAdminAllByUserRealName(String userRealName);
    public WebUser getByName(String webUserByName);
}
