package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.CheckTable;
import com.cbrc.plato.core.basic.model.ReportCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IListCheckService {
    List<CheckTable> checkTableForSource (List<CheckTable>  checkTables);

    List<CheckTable> parseReportCells1( List<CheckTable>  checkTables, List<ReportCell>  reportCells,String time,int bankId) throws Exception;

    void insertBatchList(List<CheckTable> checkTableList);

    ArrayList<CheckTable> selectByMouldId(Integer moudId);

    int updateByPrimaryKeySelective(CheckTable record);
    int insertSelective(CheckTable record);
    int deleteByPrimaryKey(Integer id);
    List<CheckTable> fuzzySearching(Integer mould_id,String projectTargetName,String projectName,String toCheckTarget,String checkTarget);
    int deleteByPrimaryKeyAll(Integer moudId);
}

