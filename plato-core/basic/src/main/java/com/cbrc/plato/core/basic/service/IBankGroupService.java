package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.BankGroup;

import java.util.List;

public interface IBankGroupService {
    public BankGroup getByName(String name);

    public BankGroup getById(int id);

    List<BankGroup> getBankGroupList();

    List<BankGroup> getBankGroupListNew(Integer roleId);
}
