package com.cbrc.plato.core.east.dao;

import com.cbrc.plato.core.east.model.EastUser;
import com.cbrc.plato.core.east.model.EastUserExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EastUserMapper {
    long countByExample(EastUserExample example);

    int deleteByExample(EastUserExample example);

    int deleteByPrimaryKey(Integer userId);

    int insert(EastUser record);

    int insertSelective(EastUser record);

    List<EastUser> selectByExample(EastUserExample example);

    EastUser selectByPrimaryKey(Integer userId);

    int updateByExampleSelective(@Param("record") EastUser record, @Param("example") EastUserExample example);

    int updateByExample(@Param("record") EastUser record, @Param("example") EastUserExample example);

    int updateByPrimaryKeySelective(EastUser record);

    int updateByPrimaryKey(EastUser record);
}