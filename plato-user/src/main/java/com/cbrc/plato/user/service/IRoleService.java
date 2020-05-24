/**
 * @Auther: heyong
 * @Date: 2019/3/19 09:30
 * @Description:
 */
package com.cbrc.plato.user.service;

import com.cbrc.plato.user.model.Role;

import java.util.ArrayList;
import java.util.List;

public interface IRoleService {
    public List<Role> getByUserId(int uid);
    public Role getByRoleId(Integer rid);
    public Role getByName(String roleByName);
    public ArrayList<Role> getRoleList();
    List<Role> selectAllByRoleName(String roleName);
    public int intsertRole(Role role);
    public int deleteById(Integer id);
    public int update(Role role);
}
