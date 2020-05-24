package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.BankGroupMapper;
import com.cbrc.plato.core.basic.model.BankGroup;
import com.cbrc.plato.core.basic.service.IBankGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankGroupServiceImpl implements IBankGroupService {
    @Autowired
    BankGroupMapper bgMapper;

    @Override
    public BankGroup getByName(String name) {
        return bgMapper.selectByName(name);
    }

    @Override
    public BankGroup getById(int id) {
        return bgMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<BankGroup> getBankGroupList() {
        return bgMapper.selectAll();
    }

    @Override
    public List<BankGroup> getBankGroupListNew(Integer roleId) {
        return bgMapper.selectAllNew(roleId);
    }
}
