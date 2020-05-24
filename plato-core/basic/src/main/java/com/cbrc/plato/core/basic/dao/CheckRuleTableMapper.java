package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckRuleTableMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CheckRuleTable record);

    int insertSelective(CheckRuleTable record);

    CheckRuleTable selectByPrimaryKey(Integer id);

    CheckRuleTable selectByRuleNameAndUser(String ruleName,Integer uid);

    int updateByPrimaryKeySelective(CheckRuleTable record);

    int updateByPrimaryKey(CheckRuleTable record);

    List<CheckRuleTable> selectAllByRuleName(String ruleName);
}