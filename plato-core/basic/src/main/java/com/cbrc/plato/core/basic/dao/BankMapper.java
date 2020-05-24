package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.Bank;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bank record);

    int insertSelective(Bank record);

    Bank selectByPrimaryKey(Integer id);

    Bank selectByName(String name);

    List<Bank> selectAll();

    int updateByPrimaryKeySelective(Bank record);

    int updateByPrimaryKey(Bank record);

    List<Bank> selectByGroupId(int id);
}