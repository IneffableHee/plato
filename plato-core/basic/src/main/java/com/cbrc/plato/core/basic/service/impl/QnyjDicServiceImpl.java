package com.cbrc.plato.core.basic.service.impl;


import com.cbrc.plato.core.basic.dao.QnyjDicMapper;
import com.cbrc.plato.core.basic.model.QnyjDic;
import com.cbrc.plato.core.basic.service.IqnyjDicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class QnyjDicServiceImpl implements IqnyjDicService {
    @Autowired
    QnyjDicMapper qnyjDicMapper;

    @Override
    public int deleteByPrimaryKey(Integer dicId) {
        return this.qnyjDicMapper.deleteByPrimaryKey (dicId);
    }

    @Override
    public int insert(QnyjDic record) {
        return this.qnyjDicMapper.insert (record);
    }

    @Override
    public int insertSelective(QnyjDic record) {
        return this.qnyjDicMapper.insertSelective (record);
    }

    @Override
    public QnyjDic selectByPrimaryKey(Integer dicId) {
        return this.qnyjDicMapper.selectByPrimaryKey (dicId);
    }

    @Override
    public int updateByPrimaryKeySelective(QnyjDic record) {
        return this.qnyjDicMapper.updateByPrimaryKeySelective (record);
    }

    @Override
    public int updateByPrimaryKey(QnyjDic record) {
        return this.qnyjDicMapper.updateByPrimaryKey (record);
    }

    @Override
    public QnyjDic selectByQnyjDicName(String dicName) {
        return this.qnyjDicMapper.selectByQnyjDicName (dicName);
    }

    @Override
    public ArrayList<QnyjDic> selectAll() {
        return this.qnyjDicMapper.selectAll ();
    }
}
