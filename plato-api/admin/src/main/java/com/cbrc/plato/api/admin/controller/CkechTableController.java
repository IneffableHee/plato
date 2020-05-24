package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.ICheckRuleTableService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.checktable.CheckTableUtil;
import com.cbrc.plato.util.datarule.report.*;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.redis.RedisCacheService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/check")
public class CkechTableController {
    @Autowired
    RedisCacheService redisCacheService;

    @Autowired
    ICheckRuleTableService checkRuleTableService;

    @Autowired
    IBankService bankService;

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IPermissionService permissionService;

    @RequestMapping("/local/report/upload")
    PlatoBasicResult reportUpload(@RequestParam("file") MultipartFile file){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null){
            return PlatoResult.unauthenticatedResult();
        }
        String fileName = file.getOriginalFilename();//文件名
        log.info(fileName);
        if(!FileUtil.isExcelFile(fileName)){
            log.info(fileName+"is not an excel file");
            return PlatoResult.failResult(fileName+"is not an excel file");
        }

        SourceTable sourceTable = FileUtil.parseTableInfoFromFileName(fileName);
        if(sourceTable == null){
            log.info("Excel文件命名不符合规范！");
            return PlatoResult.failResult("Excel文件命名不符合规范！");
        }
        String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId()+"\\"+sourceTable.getBankName()+"\\"+sourceTable.getTime()+"\\";
        try {
            FileUtil.uploadFile(file.getBytes(),path, fileName);
        }catch (Exception e){
            log.info("保存失败："+e.toString());
            log.info("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
            return PlatoResult.failResult("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
        }
        log.info("上传成功："+ sourceTable.getBankName()+"-"+sourceTable.getTable()+"-"+sourceTable.getTime());
        return PlatoResult.successResult("上传成功！");
    }

    @RequestMapping("/system/start")
    PlatoBasicResult systemCheckStart(String bankName,String time,Integer ruleType,String ruleName,Integer ruleId){
        System.out.println("systemCheckStart");
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        Bank bank = bankService.getByName(bankName);
        if(bank == null)
            return PlatoResult.failResult("机构错误，系统中不存在此机构！"+bankName);

        String path=Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId();
        CheckRuleTable checkRuleTable = null;
        if(ruleType == 0){      //本地规则
            String rulePath = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId()+"\\rule\\";
//            String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-1\\rule\\";
            checkRuleTable = CheckTableUtil.createCheckRuleTableWithExcel(rulePath+ruleName);
            if(checkRuleTable==null){
                return PlatoResult.failResult("审表规则文件不符合规范！");
            }else{
                log.info(checkRuleTable.getRuleName());
                for(CheckRuleInfo checkRuleInfo:checkRuleTable.getCheckRuleInfoList()){
                    log.info(checkRuleInfo.getBanks()+"-"+checkRuleInfo.getType()+"-"+checkRuleInfo.getSource());
                }
            }
        }else if(ruleType == 1){
            checkRuleTable = checkRuleTableService.getById(ruleId);
        }else {
            return PlatoResult.failResult("参数错误！");
        }

        return sysSingleStartCheck(checkRuleTable,bankName,time);
    }

    /*
     * @param
     * type 0:本地规则，需上传本地规则文件ruleName
     * type 1:服务器规则，需选择服务器规则文件ruleId
     * */
    @RequestMapping("/local/start")
    PlatoBasicResult localCheckStart(Integer ruleType,String ruleName,Integer ruleId){
        log.info("localCheckStart "+ruleType+","+ruleName+","+ruleId);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        CheckRuleTable checkRuleTable = null;
        if(ruleType == 0){      //本地规则
            String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId()+"\\rule\\";
//            String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-1\\rule\\";
            checkRuleTable = CheckTableUtil.createCheckRuleTableWithExcel(path+ruleName);
            if(checkRuleTable==null){
                return PlatoResult.failResult("审表规则文件不符合规范！");
            }else{
                log.info(checkRuleTable.getRuleName());
                for(CheckRuleInfo checkRuleInfo:checkRuleTable.getCheckRuleInfoList()){
                    log.info(checkRuleInfo.getBanks()+"-"+checkRuleInfo.getType()+"-"+checkRuleInfo.getSource());
                }
            }
        }else if(ruleType == 1){
            checkRuleTable = checkRuleTableService.getById(ruleId);
        }else {
            return PlatoResult.failResult("参数错误！");
        }

        String path=Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId();
//        String path=Common.CHECK_TEMPORARY_FILE_PATH+"checktable-1";
        List<String> fileNameList = new ArrayList<>();
        FileUtil.getAllFileName(path, (ArrayList<String>) fileNameList);
        if(fileNameList.size()==0) {
            FileUtil.removeDir(path);
            return PlatoResult.failResult("当前文件夹中无符合系统命名规则的excel文件");
        }

        SourceMatrix sourceMatrix = new SourceMatrix();
        List<SourceTable> sourceTableList = new ArrayList<>();
        for(String fileName:fileNameList){
            SourceTable sourceTable = FileUtil.parseTableInfoFromFileName(fileName);
            if(sourceTable!=null){
                sourceTable.setUrl(path+"\\"+sourceTable.getBankName()+"\\"+sourceTable.getTime()+"\\"+fileName);
                sourceTableList.add(sourceTable);
            }
        }
        if(sourceTableList.size()==0) {
            FileUtil.removeDir(path);
            return PlatoResult.failResult("当前文件夹中无符合系统命名规则的excel文件");
        }

        for(SourceTable sourceTable:sourceTableList){
            log.info(sourceTable.getBankName()+"-"+sourceTable.getTime()+"-"+sourceTable.getTable()+"-"+sourceTable.getUrl());
        }
        sourceMatrix.setSourceTables(sourceTableList);
        return singleStartCheck(sourceMatrix,checkRuleTable,path);
//        FileUtil.removeDir(path);
//        return PlatoResult.successResult();
    }

    PlatoBasicResult sysSingleStartCheck(CheckRuleTable checkRuleTable,String bankName,String time){
        List<CheckRuleInfo> checkRuleInfos = checkRuleTable.getCheckRuleInfoList();
        ReportTable reportTable = new ReportTable();
        reportTable.setTime(time);
        List<ReportCell> reportCells = new ArrayList<>();
        for(CheckRuleInfo checkRuleInfo:checkRuleInfos){
            if(!DateUtils.isQuarter(time)){
                if(checkRuleInfo.getTimes()=="季报")
                    continue;
            }
            if(checkRuleInfo.getSource()!=null&& DataRuleUtil.isDataRule(checkRuleInfo.getSource())){
                ReportCell reportCell = new ReportCell();
                reportCell.setBankName(bankName);
                reportCell.setTatalSource(checkRuleInfo.getSource());
                reportCell.setRuleSource(checkRuleInfo.getSource());
                reportCells.add(reportCell);
                log.info("getSource:"+bankName+","+checkRuleInfo.getSource());
            }
            if(checkRuleInfo.getTarget()!=null&&DataRuleUtil.isDataRule(checkRuleInfo.getTarget())){
                ReportCell reportCell = new ReportCell();
                reportCell.setBankName(bankName);
                reportCell.setTatalSource(checkRuleInfo.getTarget());
                reportCell.setRuleSource(checkRuleInfo.getTarget());
                reportCells.add(reportCell);
                log.info("getSource:"+bankName+","+checkRuleInfo.getTarget());
            }
        }
        reportTable.setReportCells(reportCells);
        reportTable.expressionDataRulewithBankName();
        SourceMatrix tableCellsMatrix = new SourceMatrix(reportTable);
        try {
            tableCellsMatrix = checkRuleTableService.getCellValuesFromSysExcel(tableCellsMatrix,bankName,time);
        } catch (Exception e) {
            log.info(e.getMessage());
//            e.printStackTrace();
            return PlatoResult.failResult(e.getMessage());
        }
        checkRuleTable.setCellValues(tableCellsMatrix,reportTable);
        checkRuleTable.getRuleResult();
        checkRuleTable.setBankName(bankName);
        checkRuleTable.setTime(time);

        log.info("---------------------------result------------------------------");
        log.info(bankName+" "+time);

        for(CheckRuleInfo checkRuleInfo:checkRuleTable.getCheckRuleInfoList()){
            log.info(checkRuleInfo.getSourceName()+"\t"+checkRuleInfo.getSource()+"\t"+checkRuleInfo.getSourceValue()+"\t"+checkRuleInfo.getRule()+"\t"+checkRuleInfo.getTargetName()+"\t"+checkRuleInfo.getTarget()+"\t"+checkRuleInfo.getTargetValue()+"\t"+checkRuleInfo.getResult()+"\t"+checkRuleInfo.getFailMessage());
        }
        return PlatoResult.successResult(checkRuleTable);
    }

    PlatoBasicResult singleStartCheck(SourceMatrix fileMatrix,CheckRuleTable checkRuleTable,String path){
        String time = fileMatrix.getSourceTables().get(0).getTime();
        String bankName = fileMatrix.getSourceTables().get(0).getBankName();
        List<CheckRuleInfo> checkRuleInfos = checkRuleTable.getCheckRuleInfoList();
        ReportTable reportTable = new ReportTable();
        reportTable.setTime(time);
        List<ReportCell> reportCells = new ArrayList<>();
        for(CheckRuleInfo checkRuleInfo:checkRuleInfos){
            if(!DateUtils.isQuarter(time)){
                if(checkRuleInfo.getTimes()=="季报")
                    continue;
            }
            if(checkRuleInfo.getSource()!=null&& DataRuleUtil.isDataRule(checkRuleInfo.getSource())){
                ReportCell reportCell = new ReportCell();
                reportCell.setBankName(bankName);
                reportCell.setTatalSource(checkRuleInfo.getSource());
                reportCell.setRuleSource(checkRuleInfo.getSource());
                reportCells.add(reportCell);
                log.info("getSource:"+bankName+","+checkRuleInfo.getSource());
            }
            if(checkRuleInfo.getTarget()!=null&&DataRuleUtil.isDataRule(checkRuleInfo.getTarget())){
                ReportCell reportCell = new ReportCell();
                reportCell.setBankName(bankName);
                reportCell.setTatalSource(checkRuleInfo.getTarget());
                reportCell.setRuleSource(checkRuleInfo.getTarget());
                reportCells.add(reportCell);
                log.info("getSource:"+bankName+","+checkRuleInfo.getTarget());
            }
        }
        reportTable.setReportCells(reportCells);
        reportTable.expressionDataRulewithBankName();
        SourceMatrix tableCellsMatrix = new SourceMatrix(reportTable);
        try {
            tableCellsMatrix = checkRuleTableService.getCellValuesFromLocalExcel(fileMatrix,tableCellsMatrix);
            log.info("singleStartCheck:"+tableCellsMatrix.toString());
        } catch (Exception e) {
            log.info(e.getMessage());
//            e.printStackTrace();
            FileUtil.removeDir(path);
            return PlatoResult.failResult(e.getMessage());
        }
        checkRuleTable.setCellValues(tableCellsMatrix,reportTable);
        checkRuleTable.getRuleResult();
        checkRuleTable.setBankName(bankName);
        checkRuleTable.setTime(time);

        log.info("---------------------------result------------------------------");
        log.info(bankName+" "+time);

        for(CheckRuleInfo checkRuleInfo:checkRuleTable.getCheckRuleInfoList()){
            log.info(checkRuleInfo.getSourceName()+"\t"+checkRuleInfo.getSource()+"\t"+checkRuleInfo.getSourceValue()+"\t"+checkRuleInfo.getRule()+"\t"+checkRuleInfo.getTargetName()+"\t"+checkRuleInfo.getTarget()+"\t"+checkRuleInfo.getTargetValue()+"\t"+checkRuleInfo.getResult()+"\t"+checkRuleInfo.getFailMessage());
        }
        FileUtil.removeDir(path);
        return PlatoResult.successResult(checkRuleTable);
    }


    @RequestMapping(value="/bankList" , method = RequestMethod.GET )
    public PlatoBasicResult getCheckingBankNameList(){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null)
            return PlatoResult.unauthenticatedResult();
        List<Bank> banks = new ArrayList<>();
        Map<String,List<String>> bankTimes = new HashMap<>();

        if(currentUser.getUserName().equals("qnyj_001")||currentUser.getUserName().equals("qnyj_003")||currentUser.getUserName().equals("qnyj_002")){
            banks = bankService.getBankList();
        }else{
            List<Integer> bankGroups = permissionService.selectUserBankGroupPermission(currentUser.getId());
            for (Integer bkId:bankGroups){
                List<Bank> fbanks = bankService.getBankByGroupId (bkId);
                log.info(bkId+":"+fbanks.toString());
                banks.addAll(fbanks);
            }
        }
        List<String> excelBanks = excelFileService.getBankList();
        for(Bank bank:banks){
            for(String excelBank:excelBanks){
                if(bank.getName().equals(excelBank)||bank.getShortName().equals(excelBank)){
                    List<String> times = excelFileService.getBankTimeList(excelBank);
                    bankTimes.put(excelBank,times);
                    break;
                }
            }
        }
        Set<String> keyset=bankTimes.keySet();
        JSON json = new JSONObject();
        ((JSONObject) json).put("bankList", keyset);
        ((JSONObject) json).put("bankTimes", bankTimes);
        return PlatoResult.successResult(json);
    }
}
