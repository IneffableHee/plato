package com.cbrc.plato.core.basic.dao;


import com.cbrc.plato.core.basic.model.QnyjDic;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface QnyjDicMapper {
    int deleteByPrimaryKey(Integer dicId);

    int insert(QnyjDic record);

    int insertSelective(QnyjDic record);

    QnyjDic selectByPrimaryKey(Integer dicId);

    QnyjDic selectByQnyjDicName(String dicName);

    int updateByPrimaryKeySelective(QnyjDic record);

    int updateByPrimaryKey(QnyjDic record);

    ArrayList<QnyjDic> selectAll();
}
