package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.QnyjYuqi90EastMapper;
import com.cbrc.plato.core.basic.service.IYuqi90EastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IYuqi90EastServiceImpl implements IYuqi90EastService {
    @Autowired
    QnyjYuqi90EastMapper qnyjYuqi90EastMapper;

    @Override
    public int updateByPrimaryKey(List<com.cbrc.plato.util.datarule.model.QnyjYuqi90East> record) {
        return qnyjYuqi90EastMapper.updateByPrimaryKey(record);
    }
}
