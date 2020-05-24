package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.CheckTable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public interface CheckTableMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByPrimaryKeyAll(Integer moudId);
//    int insert(CheckTable record);

    int insertSelective(CheckTable record);

    CheckTable selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CheckTable record);

    int updateByPrimaryKey(CheckTable record);

    int insertBatchList(List<CheckTable> dataInfoList);

    ArrayList<CheckTable> selectByMouldId(Integer moudId);

    ArrayList<CheckTable> fuzzySearching(Integer mouldId,String projectTargetName,String projectName,String toCheckTarget,String checkTarget);
}
