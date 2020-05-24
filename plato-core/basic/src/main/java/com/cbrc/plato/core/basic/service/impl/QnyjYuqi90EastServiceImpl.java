package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.QnyjYuqi90EastMapper;
import com.cbrc.plato.core.basic.model.QnyjYuqi90East;
import com.cbrc.plato.core.basic.service.IQnyjYuqi90EastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QnyjYuqi90EastServiceImpl implements IQnyjYuqi90EastService {

    @Autowired
    QnyjYuqi90EastMapper qnyjYuqi90EastMapper;
    @Override
    public List<QnyjYuqi90East> selectByTime(String dataTime,String dataName) {
        return qnyjYuqi90EastMapper.selectByTime (dataTime,dataName);
    }
}
