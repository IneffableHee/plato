package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.GroupMould;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMouldMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMould record);

    int insertSelective(GroupMould record);

    GroupMould selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupMould record);

    int updateByPrimaryKey(GroupMould record);
}