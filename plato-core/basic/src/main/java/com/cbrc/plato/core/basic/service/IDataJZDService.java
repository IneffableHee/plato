package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.util.datarule.datainfo.DataJZD;

import java.util.List;

public interface IDataJZDService {
    void updates(List<DataJZD> dataJZDList);
    List<DataJZD> getByBankIdAndTime(int bankId,String time);
}
