package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.QnyjDic;

import java.util.ArrayList;

public interface IqnyjDicService {
    int deleteByPrimaryKey(Integer dicId);

    int insert(QnyjDic record);

    int insertSelective(QnyjDic record);

    QnyjDic selectByPrimaryKey(Integer dicId);

    int updateByPrimaryKeySelective(QnyjDic record);

    int updateByPrimaryKey(QnyjDic record);

    QnyjDic selectByQnyjDicName(String dicName);

    public ArrayList<QnyjDic> selectAll();
}
