package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.BankMapper;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.service.IBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankServiceImpl implements IBankService {
    @Autowired
    BankMapper bankMapper;

    @Override
    public Bank getByName(String name){return this.bankMapper.selectByName(name);}

    @Override
    public Bank getById(int id) {return this.bankMapper.selectByPrimaryKey(id);}

    @Override
    public List<Bank> getBankList() {
        return this.bankMapper.selectAll();
    }

    @Override
    public List<Bank> getBankByGroupId(int id) {
        return this.bankMapper.selectByGroupId(id);
    }
}
