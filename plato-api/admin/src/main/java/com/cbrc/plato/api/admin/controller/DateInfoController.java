package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.core.basic.model.*;
import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.datarule.datainfo.DataInfoUtil;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import com.cbrc.plato.util.datarule.report.ReportCell;
import com.cbrc.plato.util.datarule.report.ReportTable;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.datarule.report.SourceTable;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/datainfo")
public class DateInfoController {

    @Autowired
    IDataInfoService dataInfoService;

    @Autowired
    IBankService bankService;

    @Autowired
    IBankGroupService bankGroupService;

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IDataService dataService;

    @Autowired
    IDataJZDService dataJZDService;

    @RequestMapping("/generation")
    public PlatoBasicResult generation(String date) {
        log.info("generation");
        if(StringUtils.isEmpty(date)){
            return PlatoResult.failResult("日期不能为空");
        }
        log.info("date :"+date);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        List<DataInfo> dataInfoList = DateUtils.isQuarter(date)?dataInfoService.getDataInfoList(roleId):dataInfoService.getMonthDataInfoList(roleId);

        Map<Integer, Map<String, List<DataInfo>>> map = new HashMap<Integer, Map<String, List<DataInfo>>>();
        for (DataInfo dataInfo : dataInfoList) {
            if (!map.keySet().contains(dataInfo.getGroupId())) {
                map.put(dataInfo.getGroupId(), new HashMap<String, List<DataInfo>>());
            }

            Map<String, List<DataInfo>> cellMap = map.get(dataInfo.getGroupId());
            if (!cellMap.keySet().contains(dataInfo.getExcelCode())) {
                cellMap.put(dataInfo.getExcelCode(), new ArrayList<DataInfo>());
            }

            List<DataInfo> cellList = cellMap.get(dataInfo.getExcelCode());
            cellList.add(dataInfo);
        }
        List<Data> dataList1 = new ArrayList<Data>();
        List<Data> dataList = new ArrayList<Data>();
        for (Map.Entry<Integer, Map<String, List<DataInfo>>> entry : map.entrySet()) {
            Map<String, List<DataInfo>> smap = entry.getValue();
            log.info("smap.size()="+smap.size());
            for (Map.Entry<String, List<DataInfo>> entry1 : smap.entrySet()) {
                List<Bank> bankList = bankService.getBankByGroupId(entry.getKey());
                for (Bank bank : bankList) {
                    PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(), entry1.getKey(), date);
                    if(platoExcelFile ==null){
                        log.info("can not find:"+bank.getName()+","+entry1.getKey()+","+date);
                        break;
                    }
                    if(StringUtil.isNullOrEmpty(platoExcelFile.getPath())){
                        log.info("未上传:"+bank.getName()+","+entry1.getKey()+","+date);
                        break;
                    }
                    List<DataInfo> dataInfos = entry1.getValue();
                    Set<String> cellSet = null;
                    try {
                        cellSet = dataInfoService.getCellSet(dataInfos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Map<String,String> value = null;
                    try {
                        log.info("path:"+ platoExcelFile.getPath());
                        value = ExcelRead.readExcelCells(platoExcelFile.getPath(),cellSet,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        log.info("dataInfos.size()==="+dataInfos.size());
                        dataList1 = dataInfoService.getData(dataInfos,value,bank,date);
                        log.info("dataList1.size()==="+dataList1.size());
                        for(Data tt : dataList1){
                            dataList.add(tt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        if(dataList.size()>0){
//            dataService.updates(dataList);
//        }else{
//            return PlatoResult.successResult("更新数据为空，请检查excel表数据是否上传成功!");
//        }
        return PlatoResult.successResult();
    }

    @RequestMapping("/generation/new")
    public PlatoBasicResult generationNew(String date,String groupid,String bankid) {
        long start = System.currentTimeMillis();
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        log.info("generation "+date +" " +groupid);
        if(StringUtils.isEmpty(date)){
            return PlatoResult.failResult("日期不能为空");
        }

        List<DataInfo> dataInfoList = new ArrayList<>();
        Bank bank = null;

        if(groupid!= null&&!groupid.equals("-1")){
            dataInfoList = DateUtils.isQuarter(date)?dataInfoService.getDataInfoListByGroupId(Integer.parseInt(groupid),roleId):dataInfoService.getMonthDataInfoListByGroupId(Integer.parseInt(groupid),roleId);
        }else if(bankid!=null){
            bank = bankService.getById(Integer.parseInt(bankid));
            dataInfoList = DateUtils.isQuarter(date)?dataInfoService.getDataInfoListByGroupId(bank.getGroupId(),roleId):dataInfoService.getMonthDataInfoListByGroupId(bank.getGroupId(),roleId);
        }else{
            dataInfoList = DateUtils.isQuarter(date)?dataInfoService.getDataInfoList(roleId):dataInfoService.getMonthDataInfoList(roleId);
        }

        log.info("数据信息表条数:"+dataInfoList.size());
        ReportTable reportTable = new ReportTable();
        reportTable.setTime(date);
        List<ReportCell> reportCells = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();
        int i=0;
        for(DataInfo dataInfo:dataInfoList){
            String rule = DataInfoUtil.parseRule(dataInfo.getDataSource());

            if(rule==null||rule.isEmpty()){
                return PlatoResult.failResult("规则错误："+dataInfo.getDataSource());
            }
            List<Bank> banks = new ArrayList<>();
            if(bank==null){
                banks = bankService.getBankByGroupId(dataInfo.getGroupId());
            }else{
                banks.add(bank);
            }

            for(Bank tbank:banks){
                ReportCell reportCell = new ReportCell();
                reportCell.setCell(dataInfo.getExcelCode());
                reportCell.setBankId(tbank.getId());
                reportCell.setTatalSource(dataInfo.getDataSource());
                reportCell.setRuleSource(rule);
                reportCells.add(reportCell);

                Data data = new Data();
                data.setBankId(tbank.getId());
                data.setBankName(tbank.getName());
                data.setDataInfoId(dataInfo.getId());
                data.setDataName(dataInfo.getDataName());
                data.setDataTime(date);
                data.setExcelCode(dataInfo.getExcelCode());
                data.setOnlyCode(dataInfo.getOnlyCode());
                data.setParent(dataInfo.getParent());
                data.setDataType(dataInfo.getDataType());
                data.setDataRule(dataInfo.getDataSource());
                dataList.add(data);
            }
            i+=banks.size();
        }
        log.info("生成"+date+"期数据，预计生成"+i+"条。"+reportCells.size());

        reportTable.setReportCells(reportCells);
        reportTable.expressionDataRulewithBankId();
        SourceMatrix sourceMatrix = new SourceMatrix(reportTable);
        try {
            sourceMatrix = dataInfoService.getSourceMatrix(sourceMatrix);
        } catch (Exception e) {
            log.info(e.getMessage());
            return PlatoResult.failResult(e.getMessage());
        }
        reportTable.setCellValues(sourceMatrix);
        dataList = dataService.getData(dataList,reportTable);
        List<Data> addDatas = new ArrayList<>();
        for(Data data:dataList){
            if(data.getDataValue()!=null&&!data.getDataValue().isEmpty()&&!StringUtils.isBlank(data.getDataValue())&&data.getDataValue()!="NullValue"){
                addDatas.add(data);
            }else{
                DataInfo dataInfo = dataInfoService.getById(data.getDataInfoId());
            }
        }
        log.info("生成成功"+addDatas.size()+"条，失败"+(dataList.size()-addDatas.size())+"条");
        if(addDatas.size()>0){
            try {
                dataService.updates(addDatas);
            }catch (Exception e){
                log.info("数据插入失败======"+e.getMessage());
                return PlatoResult.failResult("数据插入失败，请联系管理员！");
            }
        }else{
            return PlatoResult.failResult("未生成数据，请检查excel表数据是否上传成功!");
        }
        if(DateUtils.isQuarter(date)){
            generationJZD(date);
        }
        long end = System.currentTimeMillis();
        log.info("程序insert数据运行时间：" + (end - start) + "ms,");
        JSON json = new JSONObject();
        ((JSONObject) json).put("message", "生成成功"+addDatas.size()+"条，失败"+(dataList.size()-addDatas.size())+"条");
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
        return PlatoResult.successResult(json);
    }


    /*根据ID、日期生成数据*/
    @RequestMapping("/singleGeneration")
    public PlatoBasicResult generationByDataInfoIdAndTime(int id, String startTime, String endTime) {
        log.info("generationByDataInfoIdAndTime id:"+id+",startTime:"+startTime+",endTime:"+endTime);
        long startTime1 = System.currentTimeMillis();

        DataInfo dataInfo = dataInfoService.getById(id);
        if(null == dataInfo){
            return PlatoResult.failResult("数据信息不存在");
        }
        String rule = DataInfoUtil.parseRule(dataInfo.getDataSource());
        if(rule==null||rule.isEmpty()){
            return PlatoResult.failResult("规则错误："+dataInfo.getDataSource());
        }

        List<String> dbTimeList = excelFileService.getExcelTimeList();
        List<String> uTimeList = new ArrayList<>();

        if(StringUtils.isBlank(startTime)&&StringUtils.isBlank(endTime)){
            uTimeList.addAll(dbTimeList);
        }else if(StringUtils.isBlank(startTime)){
            log.info("startTime null");
            startTime = dbTimeList.get(0);
            uTimeList = DateUtils.getTimeList(startTime,endTime);
        }else if(StringUtils.isBlank(endTime)){
            log.info("endTime null");
            endTime = dbTimeList.get(dbTimeList.size()-1);
            uTimeList = DateUtils.getTimeList(startTime,endTime);
        }else {
            uTimeList = DateUtils.getTimeList(startTime,endTime);
        }

        List<Data> addDatas = new ArrayList<>();
        List<SourceTable> notUplaod = new ArrayList<>();
        for(String time:uTimeList){
            List<Data> dataList = new ArrayList<>();
            log.info("time:"+time);
            ReportTable reportTable = new ReportTable();
            List<ReportCell> reportCells = new ArrayList<>();
            reportTable.setTime(time);
            if(dataInfo.getDataType() == "季报"){
                if(!DateUtils.isQuarter(time)){
                    continue;
                }
            }
            List<Bank> bankList = bankService.getBankByGroupId(dataInfo.getGroupId());
            for (Bank bank : bankList) {
                PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(), dataInfo.getExcelCode(), time);
                if(platoExcelFile ==null){
                    log.info("不存在:"+bank.getName()+","+dataInfo.getExcelCode()+","+time);
                    break;
                }
                if(StringUtil.isNullOrEmpty(platoExcelFile.getPath())){
                    log.info("未上传:"+bank.getName()+","+dataInfo.getExcelCode()+","+time);
                    break;
                }
                ReportCell reportCell = new ReportCell();
                reportCell.setCell(dataInfo.getExcelCode());
                reportCell.setBankId(bank.getId());
                reportCell.setTatalSource(dataInfo.getDataSource());
                reportCell.setRuleSource(rule);
                reportCells.add(reportCell);

                Data data = new Data();
                data.setBankId(bank.getId());
                data.setBankName(bank.getName());
                data.setDataInfoId(dataInfo.getId());
                data.setDataName(dataInfo.getDataName());
                data.setDataTime(time);
                data.setExcelCode(dataInfo.getExcelCode());
                data.setOnlyCode(dataInfo.getOnlyCode());
                data.setParent(dataInfo.getParent());
                data.setDataType(dataInfo.getDataType());
                data.setDataRule(dataInfo.getDataSource());
                dataList.add(data);
            }
            log.info("生成"+time+"期数据，预计生成"+reportCells.size()+"条。");
            reportTable.setReportCells(reportCells);
            reportTable.expressionDataRulewithBankId();
            SourceMatrix sourceMatrix = new SourceMatrix(reportTable);
            try {
                sourceMatrix = dataInfoService.getSourceMatrix(sourceMatrix);
            } catch (Exception e) {
                log.info(e.getMessage());
//            e.printStackTrace();
                return PlatoResult.failResult(e.getMessage());
            }

            reportTable.setCellValues(sourceMatrix);
            dataList = dataService.getData(dataList,reportTable);
            for(Data data:dataList){
                if(data.getDataValue()!=null&&!data.getDataValue().isEmpty()&&!StringUtils.isBlank(data.getDataValue())&&data.getDataValue()!="NullValue"){
                    addDatas.add(data);
                }
//            log.info(data.getBankName()+"-"+data.getDataRule()+":"+data.getDataValue());
            }
            if(sourceMatrix.getNotUplaod().size()>0){
                notUplaod.addAll(sourceMatrix.getNotUplaod());
            }
        }


        log.info("生成成功"+addDatas.size()+"条.");

        if(addDatas.size()>0){
            try {
                dataService.updates(addDatas);
            }catch (Exception e){
                return PlatoResult.failResult("数据插入失败，请联系管理员！");
            }
        }else{
            return PlatoResult.failResult("未生成数据，请检查excel表是否上传成功!");
        }
        long end = System.currentTimeMillis();
        log.info("程序insert数据运行时间：" + (end - startTime1) + "ms,");

        JSON json = new JSONObject();
        ((JSONObject) json).put("message", "生成成功"+addDatas.size()+"条。");
        if(notUplaod.size()>0){
            JSONArray array = new JSONArray();
            for(SourceTable sourceTable:notUplaod){
                JSON sjson = new JSONObject();
                ((JSONObject) sjson).put("bank",sourceTable.getBankName());
                ((JSONObject) sjson).put("time",sourceTable.getTime());
                ((JSONObject) sjson).put("table",sourceTable.getTable());
                array.add(sjson);
            }
            ((JSONObject) json).put("notup", array);
            return PlatoResult.successResult(json);
        }
        log.info("AACC");
        return PlatoResult.successResult(json);

    }


    @RequestMapping("/dataInfoListPage")
    public PlatoBasicResult dataInfoListPage(Integer  pageNo,Integer  pageSize ,String dataName,Integer groupId){
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        PageHelper.startPage(pageNo,pageSize);
        List<DataInfo> dataInfolList = dataInfoService.dataInfoListPageByCode (dataName,groupId,roleId);
        PageInfo<DataInfo> dataPageInfo = new PageInfo<DataInfo> (dataInfolList);
        return PlatoResult.successResult(dataPageInfo);
    }


    @RequestMapping("/editDataInfo")
    public PlatoBasicResult editDataInfo(DataInfo  dataInfo){
        log.info (dataInfo.getDataName ());
        dataInfoService.updateDataInfo (dataInfo);
        return PlatoResult.successResult();
    }


    @RequestMapping("/addDataInfo")
    public PlatoBasicResult addDataInfo(DataInfo  dataInfo) {
        DataInfo dataInfos= dataInfoService.getByOnlyCodeAndGroupId(dataInfo.getOnlyCode(),dataInfo.getGroupId());
        if(dataInfos != null){
            dataInfo.setId(dataInfos.getId());
            dataInfoService.updateDataInfo(dataInfo);
        }else{
            //根据groupId分组排序取Id最大的 20191011
            DataInfo dataInfo1 =   dataInfoService.selectOrderByMaxId (dataInfo.getGroupId ());
            String getGroupChart =String.valueOf( dataInfo1.getOnlyCode ().charAt(0));//获取字符串的第一个字符
            // 设置onlyCode 自动增加  +1   20191011
            int onlycode = Integer.parseInt(dataInfo1.getOnlyCode ().replace (getGroupChart,"")) +1;
            String strOnlyCode =getGroupChart + String.format("%04d", onlycode);
            dataInfo.setOnlyCode (strOnlyCode);
            dataInfoService.insertSelective (dataInfo);
        }
        return PlatoResult.successResult ("新增取数规则成功！");
    }

        @RequestMapping("/bankGroupList")
        public PlatoBasicResult bankGroupList(){
            User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
            int roleId = currentUser.getUserRoleId();
            List<BankGroup> bankGroupList = dataInfoService.selectBankGroupList(roleId);
            return PlatoResult.successResult(bankGroupList);
        }


        @RequestMapping("/allEditDataInfo")
        public PlatoBasicResult allEditDataInfo(DataInfo  dataInfo){
            log.info ("-----------------------allEditDataInfo-------------------------");
            List<DataInfo> dataInfoList = dataInfoService.selectAllExcelCode(dataInfo.getExcelCode().trim(),dataInfo.getGroupId(),dataInfo.getParam2());
            List<DataInfo> insertList = new ArrayList<DataInfo>();
            if(dataInfoList != null){
                //进行批量修改操作
                for(DataInfo data : dataInfoList){
                        //包含所需要修改的参数做更新操作
                        data.setDataSource(data.getDataSource().trim().replaceAll(dataInfo.getParam2().trim(),dataInfo.getDescription().trim()));
                        insertList.add(data);
                }
            }else{
                return PlatoResult.failResult("批量修改取数规则失败,您所输入的表标识【"+dataInfo.getExcelCode().trim()+"】或原取数规则【"+dataInfo.getParam2()+"】不存在！");
            }
            //数据批量更新
            dataInfoService.updates(insertList);
            return PlatoResult.successResult("批量修改取数规则成功！");
        }

    @RequestMapping("/getGenerationInfoNew")
    public PlatoBasicResult getGenerationInfoNew(String dates ,String groupId){
        log.info ("-----------------------getGenerationInfoNew-------------------------");
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        if(groupId.equals("99")){
            //生成该日期所有数据
            this.generation(dates);
        }else{
            if(StringUtils.isEmpty(dates)){
                return PlatoResult.failResult("日期不能为空");
            }
            List<DataInfo> dataInfoList = new ArrayList<DataInfo>();
            if(DateUtils.isQuarter(dates)){
                log.info("季度");
                dataInfoList = dataInfoService.getDataInfoListByGroupId(Integer.parseInt(groupId.trim()),roleId);
            }else {
                log.info("非季度");
                dataInfoList = dataInfoService.getMonthDataInfoListByGroupId(Integer.parseInt(groupId.trim()),roleId);
            }
            Map<Integer, Map<String, List<DataInfo>>> map = new HashMap<Integer, Map<String, List<DataInfo>>>();
            for (DataInfo dataInfo : dataInfoList) {
                if (!map.keySet().contains(dataInfo.getGroupId())) {
                    map.put(dataInfo.getGroupId(), new HashMap<String, List<DataInfo>>());
                }
                Map<String, List<DataInfo>> cellMap = map.get(dataInfo.getGroupId());
                if (!cellMap.keySet().contains(dataInfo.getExcelCode())) {
                    cellMap.put(dataInfo.getExcelCode(), new ArrayList<DataInfo>());
                }
                List<DataInfo> cellList = cellMap.get(dataInfo.getExcelCode());
                cellList.add(dataInfo);
            }
            List<Data> dataList1 = new ArrayList<Data>();
            List<Data> dataList = new ArrayList<Data>();
            for (Map.Entry<Integer, Map<String, List<DataInfo>>> entry : map.entrySet()) {
                Map<String, List<DataInfo>> smap = entry.getValue();
                for (Map.Entry<String, List<DataInfo>> entry1 : smap.entrySet()) {
                    List<Bank> bankList = bankService.getBankByGroupId(entry.getKey());
                    for (Bank bank : bankList) {
                        PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(), entry1.getKey(), dates);
                        if(platoExcelFile ==null){
                            log.info("can not find:"+bank.getName()+","+entry1.getKey()+","+dates);
                            break;
                        }
                        if(StringUtil.isNullOrEmpty(platoExcelFile.getPath())){
                            log.info("未上传:"+bank.getName()+","+entry1.getKey()+","+dates);
                            break;
                        }
                        List<DataInfo> dataInfos = entry1.getValue();
                        Set<String> cellSet = null;
                        try {
                            cellSet = dataInfoService.getCellSet(dataInfos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Map<String,String> value = null;
                        try {
                            log.info("path:"+ platoExcelFile.getPath());
                            value = ExcelRead.readExcelCells(platoExcelFile.getPath(),cellSet,0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            dataList1 = dataInfoService.getData(dataInfos,value,bank,dates);
                            for(Data tt : dataList1){
                                dataList.add(tt);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if(dataList.size()>0){
                dataService.updates(dataList);
            }else{
                return PlatoResult.failResult("更新数据为空，请检查excel表数据是否上传成功!");
            }
            long endTime1 = System.currentTimeMillis();
        }
        return PlatoResult.successResult("数据生成成功!");
    }

    @RequestMapping(value = "/getBankGroupNameList",method = RequestMethod.POST)
    public PlatoBasicResult getBankGroupNameList(){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        List<BankGroup> bankGroupList = new ArrayList<BankGroup>();
        try {
            bankGroupList = bankGroupService.getBankGroupListNew(roleId);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return PlatoResult.successResult(bankGroupList);
    }

    @RequestMapping("/loadAllOnlyCode")
    public PlatoBasicResult loadAllOnlyCode(){
        return PlatoResult.successResult(dataInfoService.getAllOnlyCode());
    }

    @RequestMapping("/delete")
    public  PlatoBasicResult deleteById(int id){
        DataInfo dataInfo = dataInfoService.getById(id);
        if(dataInfo!=null){
            dataInfoService.deleteAllByOnlyCode(dataInfo.getOnlyCode());
        }
        return PlatoResult.successResult();
    }

    @RequestMapping("/generation/jzd")
    public PlatoBasicResult generationJZD(String date){
        List<Bank> nshBanks = bankService.getBankByGroupId(13);
        List<Bank> xysBanks = bankService.getBankByGroupId(14);
        List<Bank> czBanks = bankService.getBankByGroupId(4);

        List<Bank> banks = new ArrayList<>();
        banks.addAll(nshBanks);
        banks.addAll(xysBanks);
        banks.addAll(czBanks);

        List<DataJZD> insertDatas = new ArrayList<>();
        for(Bank bank:banks){
            PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bank.getId(), "G14_II", date);
            if(platoExcelFile ==null){
                log.info("can not find:"+bank.getName()+","+"G14_II"+","+date);
                break;
            }
            if(StringUtil.isNullOrEmpty(platoExcelFile.getPath())){
                log.info("未上传:"+bank.getName()+","+"G14_II"+","+date);
                break;
            }

            try {
                List<DataJZD> dataJZDS = ExcelRead.readJZD(bank.getId(),date,platoExcelFile.getPath());
                insertDatas.addAll(dataJZDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(insertDatas.size()>0){
            dataJZDService.updates(insertDatas);
            return PlatoResult.successResult();
        }
        return PlatoResult.failResult("生成失败！");
    }
}
