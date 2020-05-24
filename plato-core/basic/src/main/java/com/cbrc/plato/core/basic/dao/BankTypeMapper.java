package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.util.datarule.model.BankType;

public interface BankTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankType record);

    int insertSelective(BankType record);

    BankType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankType record);

    int updateByPrimaryKey(BankType record);
}