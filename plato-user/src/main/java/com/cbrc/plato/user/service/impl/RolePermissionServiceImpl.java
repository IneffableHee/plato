package com.cbrc.plato.user.service.impl;

import com.cbrc.plato.user.dao.RolePermissionMapper;
import com.cbrc.plato.user.model.RolePermission;
import com.cbrc.plato.user.service.IRolePermissionService;
import com.cbrc.plato.util.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RolePermissionServiceImpl  implements IRolePermissionService {

    @Autowired
    RolePermissionMapper rolePermissionMapper;

    @Override
    public int insertSelective(RolePermission role) {
        return this.rolePermissionMapper.insertSelective (role);
    }

    @Override
    public List<Integer> getPermisionsByRoleId(int rid) {
        return this.rolePermissionMapper.selectPermisionsByRoleId(rid);
    }

    @Override
    public int setRolePermisionsByRoleId(int rid, List<Integer> permissions) {
        for(int i = 0;i<permissions.size();i++){
            if(permissions.get(i)!= null ){
                RolePermission old = this.rolePermissionMapper.selectByRoleIdAndPermissionId(rid,permissions.get(i));
                if(old == null){
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(rid);
                    rolePermission.setPermissionId(permissions.get(i));
                    rolePermission.setCreateTime(DateUtils.getCurrentTime());
                    this.rolePermissionMapper.insertSelective (rolePermission);
                }
            }
        }
        return 0;
    }

    @Override
    public int clearPermissionsByRoleId(int rid) {
        return this.rolePermissionMapper.deleteByRoleId(rid);
    }

    @Override
    public int deleteByRoleId(int roleId) {
        return this.rolePermissionMapper.deleteByRoleId(roleId);
    }
}
