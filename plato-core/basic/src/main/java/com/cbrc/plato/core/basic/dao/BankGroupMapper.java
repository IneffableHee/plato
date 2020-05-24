package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.BankGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankGroup record);

    int insertSelective(BankGroup record);

    BankGroup selectByPrimaryKey(Integer id);

    BankGroup selectByName(String name);

    List<BankGroup> selectAll();

    List<BankGroup> selectAllNew(@Param("roleId") Integer roleId);

    List<BankGroup> selectStatisticAll();

    int updateByPrimaryKeySelective(BankGroup record);

    int updateByPrimaryKey(BankGroup record);
}