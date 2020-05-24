package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.ReportCell;
import com.cbrc.plato.core.basic.model.ReportMould;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IListCheckMouldService {
    ReportMould getById(int id);

    List<ReportMould> getAll();

    Set<String> getAllTableCellSet(List<ReportCell> reportCells);

    Set<String> getMonthTableCellSet(List<ReportCell> reportCells);

    Set<String> getAllTableSet(List<ReportCell> reportCells);

    Set<String> getMonthTableSet(List<ReportCell> reportCells);

    List<CellOperation> getAllOperation(List<ReportCell> reportCells);

    List<CellOperation> getMonthOperation(List<ReportCell> reportCells);

    Set<String> getMathOperationCells(String operation);

    int insert(ReportMould reportMould) throws Exception;

    int generationReport(ReportMould mould, List<ReportCell> reportCells, String outPath) throws Exception;

    Map<String,String> getTableCellValues(int bankId, Set<String> tableCells, Set<String> tables, String time) throws Exception;

//    List<CellOperation> getOperationValues(int bankId, List<CellOperation> cellOperations, String time) throws Exception;

    List<CellOperation> getOperationValues1(int bankId, List<CellOperation> cellOperations, String time) throws Exception;

}
