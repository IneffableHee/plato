package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;

import java.util.List;

public interface ICheckRuleInfoService {
    int inserts(List<CheckRuleInfo> checkRuleInfos);

    List<CheckRuleInfo> getByRuleTableId(Integer rtId);
}
