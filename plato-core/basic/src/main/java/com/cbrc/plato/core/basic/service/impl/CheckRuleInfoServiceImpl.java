package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.CheckRuleInfoMapper;
import com.cbrc.plato.core.basic.service.ICheckRuleInfoService;
import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CheckRuleInfoServiceImpl implements ICheckRuleInfoService{
    @Autowired
    CheckRuleInfoMapper checkRuleInfoMapper;

    @Override
    public int inserts(List<CheckRuleInfo> checkRuleInfos){
        return this.checkRuleInfoMapper.inserts(checkRuleInfos);
    }

    @Override
    public List<CheckRuleInfo> getByRuleTableId(Integer rtId) {
        return this.checkRuleInfoMapper.selectByRuleTableId(rtId);
    }
}
