package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.DataJZDMapper;
import com.cbrc.plato.core.basic.service.IDataJZDService;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataJZDServiceImpl implements IDataJZDService {
    @Autowired
    DataJZDMapper dataJZDMapper;

    @Override
    public void updates(List<DataJZD> dataJZDList) {
        this.dataJZDMapper.inserts(dataJZDList);
    }

    @Override
    public List<DataJZD> getByBankIdAndTime(int bankId, String time) {
        return this.dataJZDMapper.selectByBankIdAndTime(bankId,time);
    }
}
