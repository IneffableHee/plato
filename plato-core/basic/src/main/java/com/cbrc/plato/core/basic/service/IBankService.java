package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.Bank;

import java.util.List;

public interface IBankService {

    Bank getByName(String name);

    Bank getById(int id);

    List<Bank> getBankList();

    List<Bank> getBankByGroupId(int id);
}
