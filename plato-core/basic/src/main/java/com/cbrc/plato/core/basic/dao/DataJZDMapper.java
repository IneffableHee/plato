package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataJZDMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DataJZD record);

    int insertSelective(DataJZD record);

    DataJZD selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DataJZD record);

    int updateByPrimaryKey(DataJZD record);

    int inserts(List<DataJZD> item);

    List<DataJZD> selectByBankIdAndTime(int bankId, String time);
}