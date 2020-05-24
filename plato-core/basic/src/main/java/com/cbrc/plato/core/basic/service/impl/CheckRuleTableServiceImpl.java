package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.CheckRuleTableMapper;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.ICheckRuleInfoService;
import com.cbrc.plato.core.basic.service.ICheckRuleTableService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.report.SourceCell;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.datarule.report.SourceTable;
import com.cbrc.plato.util.excel.ExcelRead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CheckRuleTableServiceImpl implements ICheckRuleTableService {
    @Autowired
    CheckRuleTableMapper checkRuleTableMapper;

    @Autowired
    ICheckRuleInfoService checkRuleInfoService;

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IBankService bankService;

    @Override
    public int insert(CheckRuleTable checkRuleTable) {
        return this.checkRuleTableMapper.insertSelective(checkRuleTable);
    }

    @Override
    public CheckRuleTable getByRuleNameAndUser(String ruleName, Integer uid) {
        return this.checkRuleTableMapper.selectByRuleNameAndUser(ruleName,uid);
    }

    @Override
    public CheckRuleTable getById(Integer id) {
        CheckRuleTable checkRuleTable = this.checkRuleTableMapper.selectByPrimaryKey(id);
        if(checkRuleTable==null){
            return null;
        }
        List<CheckRuleInfo> checkRuleInfoList = this.checkRuleInfoService.getByRuleTableId(id);
        checkRuleTable.setCheckRuleInfoList(checkRuleInfoList);
        return checkRuleTable;
    }

    @Override
    public List<CheckRuleTable> getAllByRuleName(String ruleName) {
        return checkRuleTableMapper.selectAllByRuleName(ruleName);
    }

    @Override
    public SourceMatrix getCellValuesFromLocalExcel(SourceMatrix localFileMatrix, SourceMatrix tableCellsMatrix) throws Exception {
        log.info("getRuleValueFromLocalExcel");
        for(SourceTable sourceTable:tableCellsMatrix.getSourceTables()){
            System.out.println(sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime()+sourceTable.getCells().toString());
        }
        for(SourceTable sourceTable:tableCellsMatrix.getSourceTables()){
            String path = null;
            if(!sourceTable.getTime().equals(tableCellsMatrix.getTime())){      //往期数据，从服务器取数，如需关闭，则屏蔽此段
                Bank bank = bankService.getByName(sourceTable.getBankName());
                if(bank == null){
                    log.info(sourceTable.getBankName()+"机构在系统中不存在，无法渠道往期数据："+sourceTable.getTable()+"-"+sourceTable.getTime()+"-"+sourceTable.getCells().toString());
                    for(SourceCell sourceCell:sourceTable.getSourceCells()){
                        sourceCell.setValue("notUpload");
                    }
                    continue;
                }
                PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(),sourceTable.getTable(),sourceTable.getTime());
                if(platoExcelFile==null||platoExcelFile.getPath()==null){
                    log.info(sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime()+"-"+sourceTable.getCells().toString()+"为上传系统，无法取到往期数据！");
                    for(SourceCell sourceCell:sourceTable.getSourceCells()){
                        sourceCell.setValue("notUpload");
                    }
                    continue;
                }
                path = platoExcelFile.getPath();
            }else{
                SourceTable localTable = localFileMatrix.getLocalSourceTableByTimeAndCode(sourceTable.getTime(),sourceTable.getTable());
                if(localTable == null){
                    log.info("找不到文件："+sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime()+"-"+sourceTable.getUrl());
                    for(SourceCell sourceCell:sourceTable.getSourceCells()){
                        sourceCell.setValue("notUpload");
                    }
                    continue;
                }
                path = localTable.getUrl();
            }

            if(path==null||path.isEmpty())
                continue;
            Map<String,String> cellValues = new HashMap<>();
            try {
                log.info("Read "+sourceTable.getTable()+sourceTable.getCells().toString());
                cellValues = ExcelRead.readExcelCells(path,sourceTable.getCells(),0);
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    log.info("cell: "+sourceCell.getCell()+", value:"+cellValues.get(sourceCell.getCell()));
                    sourceCell.setValue(cellValues.get(sourceCell.getCell()));
                }
            } catch (Exception e) {
                log.info("文件读取失败或找不到文件："+sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime()+"-"+sourceTable.getUrl());
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    sourceCell.setValue("notUpload");
                }
                e.printStackTrace();
            }
        }

        return tableCellsMatrix;
    }

    @Override
    public SourceMatrix getCellValuesFromSysExcel(SourceMatrix tableCellsMatrix,String bankName,String time) throws Exception {
        log.info("getCellValuesFromSysExcel");
        Bank bank = bankService.getByName(bankName);
        if(bank==null)
            throw new Exception("机构错误："+bankName);

        for(SourceTable sourceTable:tableCellsMatrix.getSourceTables()){
            PlatoExcelFile excelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(),sourceTable.getTable(),time);
            if(excelFile==null){
                log.info("找不到文件："+sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime()+"-"+sourceTable.getUrl());
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    sourceCell.setValue("notUpload");
                }
                continue;
            }
            Map<String,String> cellValues = new HashMap<>();
            try {
                log.info("Read "+bankName+time+sourceTable.getTable()+sourceTable.getCells().toString());
                cellValues = ExcelRead.readExcelCells(excelFile.getPath(),sourceTable.getCells(),0);
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    log.info("cell: "+sourceCell.getCell()+", value:"+cellValues.get(sourceCell.getCell()));
                    sourceCell.setValue(cellValues.get(sourceCell.getCell()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tableCellsMatrix;
    }
}
