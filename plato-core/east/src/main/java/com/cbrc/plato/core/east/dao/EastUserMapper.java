package com.cbrc.plato.core.east.dao;

import com.cbrc.plato.core.east.model.EastUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EastUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(EastUser record);

    int insertSelective(EastUser record);

    EastUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(EastUser record);

    int updateByPrimaryKey(EastUser record);
}