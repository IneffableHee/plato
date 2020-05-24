package com.cbrc.plato.core.basic.dao;


import com.cbrc.plato.core.basic.model.QnyjYuqi90East;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface QnyjYuqi90EastMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(QnyjYuqi90East record);

    int insertSelective(QnyjYuqi90East record);

    QnyjYuqi90East selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(QnyjYuqi90East record);

    int updateByPrimaryKey(List<com.cbrc.plato.util.datarule.model.QnyjYuqi90East> record);

    ArrayList<QnyjYuqi90East> selectByTime(@Param("dataTime")String dataTime,@Param("dataName") String dataName);
}
