/**
 * @Auther: heyong
 * @Date: 2019/3/19 09:42
 * @Description:
 */
package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.RoleMapper;
import com.cbrc.plato.user.model.Role;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IRoleService;
import com.cbrc.plato.user.service.IUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    IUserService userService;

    public List<Role> getByUserId(int uid){
        User user = userService.getById(uid);
        List<Integer> roleIdList = user.getRoleList();
        if(CollectionUtils.isNotEmpty(roleIdList)){
            List<Role> roleList=new ArrayList<>();
            for(Integer rid:roleIdList){
                Role role = this.roleMapper.selectByPrimaryKey(rid);
                if(role!=null)
                    roleList.add(role);
            }
            return roleList;
        }
        return null;
    }

    @Override
    public Role getByRoleId(Integer rid) {
        return this.roleMapper.selectByPrimaryKey(rid);
    }

    @Override
    public ArrayList<Role> getRoleList(){
        return this.roleMapper.selectAll();
    }

    @Override
    public List<Role> selectAllByRoleName(String roleName) {
        return this.roleMapper.selectAllByRoleName(roleName);
    }

    @Override
    public Role getByName(String roleByName){
        return this.roleMapper.selectByName(roleByName);
    }

    @Override
    public int  intsertRole(Role role){
        return  this.roleMapper.insertRole(role);
    }

    @Override
    public int deleteById(Integer id) {
        return this.roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int update(Role role) {
        return this.roleMapper.updateByPrimaryKey(role);
    }

}
