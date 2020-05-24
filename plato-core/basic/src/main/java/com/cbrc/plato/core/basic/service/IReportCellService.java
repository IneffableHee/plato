package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.CheckTable;
import com.cbrc.plato.core.basic.model.ReportCell;

import java.util.List;
import java.util.Map;

public interface IReportCellService {
    List<ReportCell> parseReportCells(Map<String,String> cells);

    List<CheckTable> parseReportCells1(Map<String,String> cells);

    ReportCell cellMathematical(ReportCell reportCell, Map<String,String> tableCellValues, List<CellOperation> cellOperations) throws  Exception;

    ReportCell setWarning(ReportCell reportCell);
}
