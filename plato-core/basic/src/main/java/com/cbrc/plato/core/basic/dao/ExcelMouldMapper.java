package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.ExcelMould;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelMouldMapper {
    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeys(Integer id);

    int insert(ExcelMould record);

    int insertSelective(ExcelMould record);

    ExcelMould selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExcelMould record);

    int updateByPrimaryKey(ExcelMould record);

    List<ExcelMould> selectAll();

    List<ExcelMould> selectAlls(@Param("code")String code);

    List<ExcelMould> selectMouth();

    List<ExcelMould> selectQuarter();

    List<ExcelMould> selectByBankGroupName(String name);

    ExcelMould selectByCodeAndBGName(@Param("code") String code,@Param("name") String name);
}
