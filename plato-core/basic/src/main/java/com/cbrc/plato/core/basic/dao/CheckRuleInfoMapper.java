package com.cbrc.plato.core.basic.dao;


import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckRuleInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CheckRuleInfo record);

    int insertSelective(CheckRuleInfo record);

    int inserts(List<CheckRuleInfo> item);

    CheckRuleInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CheckRuleInfo record);

    int updateByPrimaryKey(CheckRuleInfo record);

    List<CheckRuleInfo> selectByRuleTableId(Integer rtId);
}