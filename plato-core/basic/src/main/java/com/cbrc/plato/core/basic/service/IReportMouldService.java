package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.ReportCell;
import com.cbrc.plato.core.basic.model.ReportMould;
import com.cbrc.plato.util.datarule.report.ReportTable;
import com.cbrc.plato.util.datarule.report.SourceMatrix;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IReportMouldService {
    ReportMould getById(int id);

    List<ReportMould> getAll();

    List<ReportMould> getByUserId(Integer uid);

    Set<String> getAllTableCellSet(List<ReportCell> reportCells);

    Set<String> getMonthTableCellSet(List<ReportCell> reportCells);

    Set<String> getAllTableSet(List<ReportCell> reportCells);

    Set<String> getMonthTableSet(List<ReportCell> reportCells);

    List<CellOperation> getAllOperation(List<ReportCell> reportCells);

    List<CellOperation> getMonthOperation(List<ReportCell> reportCells);

    Set<String> getMathOperationCells(String operation);

    int deleteById(Integer id);

    int insert(ReportMould reportMould) throws Exception;

    int generationReport(ReportMould mould,List<ReportCell> reportCells,String outPath) throws Exception;

    int generationReport(ReportMould mould, ReportTable reportTable, String outPath ,String name,String pareTime) throws Exception;

    Map<String,String> getTableCellValues(int bankId,Set<String> tableCells,Set<String> tables,String time) throws Exception;

    List<CellOperation> getOperationValues(int bankId, List<CellOperation> cellOperations,String time) throws Exception;

    SourceMatrix getSourceMatrix(SourceMatrix sourceMatrix) throws Exception;

    ReportTable getBankIds(ReportTable reportTable) throws Exception;
}
