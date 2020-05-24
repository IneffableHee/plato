package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.ReportGeneration;

public interface ReportGenerationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportGeneration record);

    int insertSelective(ReportGeneration record);

    ReportGeneration selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportGeneration record);

    int updateByPrimaryKey(ReportGeneration record);
}