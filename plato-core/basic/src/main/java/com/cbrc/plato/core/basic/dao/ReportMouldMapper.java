package com.cbrc.plato.core.basic.dao;


import com.cbrc.plato.core.basic.model.ReportMould;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMouldMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportMould record);

    int insertSelective(ReportMould record);

    ReportMould selectByPrimaryKey(Integer id);

    List<ReportMould> selectAll();

    List<ReportMould> selectByUserId(Integer uid);

    int updateByPrimaryKeySelective(ReportMould record);

    int updateByPrimaryKey(ReportMould record);
}