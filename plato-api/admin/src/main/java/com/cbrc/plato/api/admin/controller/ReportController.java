package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.ReportMould;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.core.basic.service.IReportCellService;
import com.cbrc.plato.core.basic.service.IReportMouldService;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.datarule.report.ReportTable;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.datarule.report.SourceTable;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    IReportMouldService reportMouldService;

    @Autowired
    IReportCellService reportCellService;

    @Autowired
    IBankService bankService;

    @Autowired
    IExcelFileService excelFileService;

    @RequestMapping("/generation")
    public PlatoBasicResult generation(int mid,String time){
        switch (mid){
            case 1:
                return czyhzyzbjcb(mid,time);
            default:
                return generalGenerationNew(mid,time);
        }
    }

    /* new api村镇银行主要指标监测统计表*/
    @RequestMapping("/czyhzyzbjcb")
    public PlatoBasicResult czyhzyzbjcb(int mid,String time){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        String name = currentUser.getUserRealName();
        Date times = new Date();
        String pareTime = DateUtils.format(times,"yyyy-MM-dd");
        log.info(pareTime);
        long startTime = System.currentTimeMillis();
        ReportMould reportMould = reportMouldService.getById(mid);
        if(reportMould == null)
            return PlatoResult.failResult("模板不存在！");

        /*读取模板表，获取取数规则！*/
        log.info(reportMould.getPath());
        ReportTable reportTable = new ReportTable(reportMould.getPath(),0);
        reportTable.setMould(reportMould.getId());
        reportTable.setTime(time);

        List<Bank> banks = bankService.getBankByGroupId(4);
        List<com.cbrc.plato.util.datarule.report.ReportCell> totalReportCells = new ArrayList<>();
        List<com.cbrc.plato.util.datarule.report.ReportCell> reportCells = reportTable.getReportCells();
        char x = 'C';
        int nameTip = 0;
        int timeTip = 0;
        for(Bank bank:banks){
            x+=1;
            for(com.cbrc.plato.util.datarule.report.ReportCell reportCell:reportCells){
                if(reportCell.getTatalSource()!=null){
                    if(reportCell.getTatalSource().contains("{{name}}")){
                        if(nameTip == 0){
                            com.cbrc.plato.util.datarule.report.ReportCell tmp = new com.cbrc.plato.util.datarule.report.ReportCell();
                            try {
                                BeanUtils.copyProperties(tmp,reportCell);
                                totalReportCells.add(tmp);
                            }catch (Exception e){
                                e.printStackTrace();
                                return PlatoResult.failResult("ReportCell错误！！");
                            }
                            nameTip++;
                        }
                        continue;
                    }
                    if(reportCell.getTatalSource().contains("{{time}}")){
                            if(timeTip == 0){
                            com.cbrc.plato.util.datarule.report.ReportCell tmp = new com.cbrc.plato.util.datarule.report.ReportCell();
                            try {
                                BeanUtils.copyProperties(tmp,reportCell);
                                totalReportCells.add(tmp);
                            }catch (Exception e){
                                e.printStackTrace();
                                return PlatoResult.failResult("ReportCell错误！！");
                            }
                            timeTip++;
                        }
                        continue;
                    }
                }
                reportCell.setCell(reportCell.getCell().replace(reportCell.getCell().charAt(0),x));
                reportCell.setBankId(bank.getId());
                reportCell.setBankName(bank.getShortName());
                if(!DateUtils.isQuarter(time)&&reportCell.isMonth()==0){
                    reportCell.setRuleSource("");
                    reportCell.setValue("");
                }

                com.cbrc.plato.util.datarule.report.ReportCell tmp = new com.cbrc.plato.util.datarule.report.ReportCell();
                try {
                    BeanUtils.copyProperties(tmp,reportCell);
                    totalReportCells.add(tmp);
                }catch (Exception e){
                    e.printStackTrace();
                    return PlatoResult.failResult("ReportCell错误！！");
                }
            }
            com.cbrc.plato.util.datarule.report.ReportCell nameCell = new com.cbrc.plato.util.datarule.report.ReportCell();
            nameCell.setCell(String.valueOf(x)+'2');
            nameCell.setValue(bank.getShortName());
            totalReportCells.add(nameCell);
        }
        reportTable.setReportCells(totalReportCells);
        reportTable.expressionDataRulewithBankId();
        SourceMatrix sourceMatrix = new SourceMatrix(reportTable);
        try {
            sourceMatrix = reportMouldService.getSourceMatrix(sourceMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return PlatoResult.failResult(e.getMessage());
        }

        reportTable.setCellValues(sourceMatrix);
        reportTable.setCreateUserAndTime(name,pareTime);
        List<SourceTable> sumTable = sourceMatrix.getSourceTables();
        int sum = 0 , not = 0;
        if(sumTable != null){
            sum = sumTable.size();
        }
        List<SourceTable> notUpload = sourceMatrix.getNotUplaod();
        if(notUpload != null){
            not = notUpload.size();
        }
        log.info("生成成功"+(sum-not)+"条，失败"+(not)+"条");
        JSON json = new JSONObject();
        ((JSONObject) json).put("message", "生成成功"+(sum-not)+"条，失败"+(not)+"条");
        String fileName = "";
        try {
            fileName= reportMould.getName().replace("2019",time)+System.currentTimeMillis()+".xlsx";
            String path = Common.TEMPORARY_FILE_PATH;
            path+=fileName;
            reportMouldService.generationReport(reportMould,reportTable,path,name,pareTime);
            log.info("success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((JSONObject) json).put("fileName",fileName);
        if(sourceMatrix.getNotUplaod().size()>0){
            JSONArray array = new JSONArray();
            for(SourceTable sourceTable:sourceMatrix.getNotUplaod()){
                JSON sjson = new JSONObject();
                ((JSONObject) sjson).put("bank",sourceTable.getBankName());
                ((JSONObject) sjson).put("time",sourceTable.getTime());
                ((JSONObject) sjson).put("table",sourceTable.getTable());
                array.add(sjson);
            }
            ((JSONObject) json).put("notup", array);
            return PlatoResult.successResult(json);
        }
        long endTime = System.currentTimeMillis();
        log.info("程序运行时间：" + (endTime - startTime) + "ms;");
        return PlatoResult.successResult(json);
    }

    public PlatoBasicResult generalGenerationNew(int mid,String time){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        String name = currentUser.getUserRealName();
        Date times = new Date();
        String pareTime = DateUtils.format(times,"yyyy-MM-dd");
        log.info(pareTime);
        ReportMould reportMould = reportMouldService.getById(mid);
        if(reportMould == null)
            return PlatoResult.failResult("模板不存在！");

        /*读取模板表，获取取数规则！*/
        log.info(reportMould.getPath());
        ReportTable reportTable = new ReportTable(reportMould.getPath(),0);
        try {
            reportMouldService.getBankIds(reportTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportTable.setMould(reportMould.getId());
        reportTable.setTime(time);
        reportTable.expressionDataRulewithBankId();

        SourceMatrix sourceMatrix = new SourceMatrix(reportTable);
        try {
            sourceMatrix = reportMouldService.getSourceMatrix(sourceMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return PlatoResult.failResult(e.getMessage());
        }
//        return PlatoResult.successResult();
        reportTable.setCellValues(sourceMatrix);
        reportTable.setCreateUserAndTime(name,pareTime);
        String fileName = "";
        try {
            fileName = reportMould.getName()+"("+time+")"+System.currentTimeMillis()+".xlsx";
            String path = Common.TEMPORARY_FILE_PATH;
            path+=fileName;
            reportMouldService.generationReport(reportMould,reportTable,path,name,pareTime);
            log.info("success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("fileName",fileName);
        return PlatoResult.successResult(json);
    }

    @RequestMapping("/down/{fileName}")
    public PlatoBasicResult downLoadReport(@PathVariable("fileName") String fileName, HttpServletRequest request, HttpServletResponse response){
        String file = fileName.substring(0,fileName.length()-18);
        file += ".xlsx";
        log.info(file+"-----"+Common.TEMPORARY_FILE_PATH+fileName);
        return FileUtil.downloadfile(response , request,file,Common.TEMPORARY_FILE_PATH+fileName);
    }
}
