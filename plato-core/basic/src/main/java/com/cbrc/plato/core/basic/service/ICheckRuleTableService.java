package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.report.SourceMatrix;

import java.util.List;

public interface ICheckRuleTableService {
    int insert(CheckRuleTable checkRuleTable);

    CheckRuleTable getByRuleNameAndUser(String ruleName,Integer uid);

    CheckRuleTable getById(Integer id);

    List<CheckRuleTable> getAllByRuleName(String ruleName);

    SourceMatrix getCellValuesFromLocalExcel(SourceMatrix localFileMatrix, SourceMatrix tableCellsMatrix) throws Exception;

    SourceMatrix getCellValuesFromSysExcel(SourceMatrix tableCellsMatrix,String bankName,String time) throws Exception;
}
