package com.cbrc.plato.core.basic.service.impl;


import com.cbrc.plato.core.basic.dao.CheckTableMapper;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.CheckTable;
import com.cbrc.plato.core.basic.model.ReportCell;
import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.util.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
 @Slf4j
public class ListCheckService implements IListCheckService {

     @Autowired
     IReportCellService reportCellService;


     @Autowired
     IReportCellService listCheckService;


    @Autowired
    ListCheckMouldService listCheckMouldService;

     @Autowired
     IBankService bankService;

     @Autowired
     IExcelFileService excelFileService;

    @Autowired
    CheckTableMapper checkTableMapper;

    @Override
    public List<CheckTable> checkTableForSource(List<CheckTable> checkTables) {
        for(CheckTable checkTable:checkTables) {

            if(checkTable.getToCheckTarget ().startsWith ("inc<")){
                String toTableForSource = checkTable.getToCheckTarget ().replace ("inc<","");
                toTableForSource =  toTableForSource.substring(0, toTableForSource.indexOf("["));
                checkTable.setToTableForSource (toTableForSource);
            }else {
                String toTableForSource =  checkTable.getToCheckTarget().substring(0, checkTable.getToCheckTarget().indexOf("["));
                checkTable.setToTableForSource (toTableForSource);
            }
            if(checkTable.getCheckTarget ().indexOf("[") != -1 ){
                String toTableForSource = checkTable.getCheckTarget () ;
                toTableForSource =  toTableForSource.substring(0, toTableForSource.indexOf("["));
                checkTable.setTableForSource (toTableForSource);
            }
        }

        return checkTables;
    }



     @Override
     public List<CheckTable> parseReportCells1(List<CheckTable>  checkTables, List<ReportCell>  reportCells ,String time,int bankId) throws Exception {
        List<CheckTable>   newChecktbales = new ArrayList<> ();
        Iterator<ReportCell> iter = null;
        ReportCell prod = null;

        /*分别获取tableCells、tables、operations，以防止重复打开表格、重复计算*/
        Set<String> tableCells = DateUtils.isQuarter(time)?listCheckMouldService.getAllTableCellSet(reportCells):listCheckMouldService.getMonthTableCellSet(reportCells);
        Set<String> tables = DateUtils.isQuarter(time)?listCheckMouldService.getAllTableSet(reportCells):listCheckMouldService.getMonthTableSet(reportCells);
        List<CellOperation> cellOperations = DateUtils.isQuarter(time)?listCheckMouldService.getAllOperation(reportCells):listCheckMouldService.getMonthOperation(reportCells);


         if(tableCells==null||tables==null){
             log.info ("-----1215412----"+1234568);
            return null;
        }
        char x = 'C';
//        List<Bank> banks = bankService.getBankByGroupId(7);
        Bank bank = bankService.getById (bankId);
//                for(Bank bank:banks) {
//                    if(bank.getId ()==33 || bank.getId ()==32) {
                        x += 1;
                        Map<String, String> tableCellValues = null;
                        try {
                            tableCellValues = listCheckMouldService.getTableCellValues (bank.getId (), tableCells, tables, time);
                            cellOperations = listCheckMouldService.getOperationValues1(bank.getId (), cellOperations, time);
                        } catch (Exception e) {
                            log.info ("---单元格未取到值：--"+e.getMessage());
                            throw new Exception(e.getMessage());
                        }
                        for (int j=0; j< reportCells.size ();j++) {
                            ReportCell reportCell = new ReportCell();
                            String strcell = reportCells.get (j).getCell ();
                            log.info ("--strcell-"+strcell);
                            strcell = strcell.replace (strcell.charAt (0), x);
                            reportCells.get (j).setCell (strcell);
//                            reportCells.get (j).setBankName (bank.getShortName ());
                            reportCells.get (j).setWarning (false);

                            if (!DateUtils.isQuarter (time) && reportCells.get (j).isMonth () == 0) {
                                reportCells.get (j).setDataRule ("");
                                reportCells.get (j).setValue ("");
                            } else {
                                try {
                                    reportCell = reportCellService.cellMathematical (reportCells.get (j), tableCellValues, cellOperations);
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    return null;
                                }
                            }
                            CheckTable checkTable = new CheckTable ();
                            checkTable.setBankName (bank.getShortName ());
                            checkTable.setCheckResults (reportCell.getValue ());
                            checkTable.setBankName (bank.getShortName ());
                            newChecktbales.add (checkTable);
                            log.info ("1-"+newChecktbales.size ());
                        }
//                    }
//                }
//        for(int i = 0;i < checkTables.size();i++){
//            log.info ("checkTables:"+checkTables.get (i).getToCheckTarget ()+":"+checkTables.get (i).getCheckRule ()+":"+checkTables.get (i).getCheckTarget ()+":"+checkTables.get (i).getMonth ()+"结果"+checkTables.get (i).getCheckResults ());
//        }
         return newChecktbales;
     }
    @Override
    public void insertBatchList(List<CheckTable> checkTableList) {
//        List<CheckTable> updateList = new ArrayList<CheckTable>();

//        if(updateList.size()>0){
//            this.dataInfoMapper.updates(updateList);
//        }
        if(checkTableList.size()>0){
            this.checkTableMapper.insertBatchList(checkTableList);
        }

    }
    @Override
    public ArrayList<CheckTable> selectByMouldId(Integer moudId) {
        return this.checkTableMapper.selectByMouldId (moudId);
    }

    @Override
    public int updateByPrimaryKeySelective(CheckTable record) {
        return this.checkTableMapper.updateByPrimaryKeySelective (record);
    }

    @Override
    public int insertSelective(CheckTable record) {
        return this.checkTableMapper.insertSelective (record);
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.checkTableMapper.deleteByPrimaryKey (id);
    }

    @Override
    public List<CheckTable> fuzzySearching(Integer mould_id,String projectTargetName, String projectName, String toCheckTarget, String checkTarget) {
        return this.checkTableMapper.fuzzySearching ( mould_id,projectTargetName,projectName,toCheckTarget,checkTarget);
    }

    @Override
    public int deleteByPrimaryKeyAll(Integer moudId) {
        return this.checkTableMapper.deleteByPrimaryKeyAll (moudId);
    }

}
