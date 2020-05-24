package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.DataInfoMapper;
import com.cbrc.plato.core.basic.model.*;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.IDataInfoService;
import com.cbrc.plato.core.basic.service.IDataService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.util.datarule.report.SourceCell;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.datarule.report.SourceTable;
import com.cbrc.plato.util.excel.ExcelCommon;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.math.MathUtil;
import com.cbrc.plato.util.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DataInfoServiceImpl implements IDataInfoService {
    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IBankService bankService;

    @Autowired
    DataInfoMapper dataInfoMapper;

    @Autowired
    IDataService dataService;

    @Override
    public List<DataInfo> getDataInfoList(Integer roleId) {
        return dataInfoMapper.selectAllDataInfo(roleId);
    }

    @Override
    public List<DataInfo> getDataInfoListByGroupId(int groupId,int roleId) {
        return dataInfoMapper.getDataInfoListByGroupId(groupId,roleId);
    }

    @Override
    public List<BankGroup> selectBankGroupList(Integer roleId) {
        return dataInfoMapper.selectBankGroupList(roleId);
    }

    public List<DataInfo> dataInfoListPageByCode(String dataName,Integer groupId , Integer roleId) {
        return dataInfoMapper.dataInfoListPageByCode(dataName,groupId,roleId);
    }

    @Override
    public List<DataInfo> getMonthDataInfoList(Integer roleId) {
        return dataInfoMapper.selectMonthDataInfo(roleId);
    }

    @Override
    public List<DataInfo> getMonthDataInfoListByGroupId(int groupId , int roleId) {
        return dataInfoMapper.getMonthDataInfoListByGroupId(groupId,roleId);
    }

    @Override
    public DataInfo getByOnlyCodeAndGroupId(String onlyCode, int gid) {
        return dataInfoMapper.selectByOnlyCodeAndGroupId(onlyCode,gid);
    }

    public DataInfo getById(int id){
        return dataInfoMapper.selectById(id);
    }

    @Override
    public DataInfo selectByOnlyCode(String onlyCode) {
        return this.dataInfoMapper.selectByOnlyCode(onlyCode);
    }

    @Override
    public List<DataInfo> getDataInfoByGroupId(int gid) {
        return dataInfoMapper.selectByGroupId(gid);
    }

    @Override
    public SourceMatrix getSourceMatrix(SourceMatrix sourceMatrix) throws Exception{
        for(SourceTable sourceTable:sourceMatrix.getSourceTables()){
            System.out.println(sourceTable.getBankId()+sourceTable.getTable()+sourceTable.getTime());
            PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(sourceTable.getBankId(),sourceTable.getTable(),sourceTable.getTime());
            if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                SourceTable notUpload = new SourceTable();
                notUpload.setBankName(bankService.getById(sourceTable.getBankId()).getName());
                notUpload.setTime(sourceTable.getTime());
                notUpload.setTable(sourceTable.getTable());
                sourceMatrix.getNotUplaod().add(notUpload);
                sourceTable.setSourceCells(null);
                System.out.println("机构："+bankService.getById(sourceTable.getBankId()).getName()+",表："+sourceTable.getTable()+"未上传,无法生成数据！");
                continue;
            }
            Map<String,String> cellValues = new HashMap<>();
            try {
                log.info("Read "+sourceTable.getTable());
                cellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),sourceTable.getCells(),0);
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    log.info("cell: "+sourceCell.getCell()+", value:"+cellValues.get(sourceCell.getCell()));
                    sourceCell.setValue(cellValues.get(sourceCell.getCell()));
                }
            } catch (Exception e) {
                SourceTable notUpload = new SourceTable();
                notUpload.setBankName(bankService.getById(sourceTable.getBankId()).getName());
                notUpload.setTime(sourceTable.getTime());
                notUpload.setTable(sourceTable.getTable());
                sourceMatrix.getNotUplaod().add(notUpload);
                sourceTable.setSourceCells(null);
                System.out.println("机构："+sourceTable.getBankId()+"表："+sourceTable.getTable()+e.getMessage());
                continue;
//                e.printStackTrace();
            }
        }
        return sourceMatrix;
    }

    @Override
    public Set<String> getCellSet(List<DataInfo> dataInfos) throws Exception {
        String math = "[+\\-*/]";              //匹配加减乘除运算
        String contrast = "^tb|^hb|^nc|^tqs|^sqs|^ncs";        //匹配同比、环比、年初
        String single = "^[a-zA-Z]?\\d+$";       //匹配单个单元格

        Set<String> cellList = new HashSet<>();
        for(DataInfo dataInfo:dataInfos){
//            log.info("@@@@@@"+dataInfo.getDataSource());
            String dataSource = dataInfo.getDataSource().replace(" ","");
            Pattern patternMath = Pattern.compile(math);
            Matcher mathMatcher = patternMath.matcher(dataSource);

            Pattern patternContrast = Pattern.compile(contrast);
            Matcher contrastMatcher = patternContrast.matcher(dataSource);

            Pattern patternSingle = Pattern.compile(single);
            Matcher singletMatcher = patternSingle.matcher(dataSource);

            if (contrastMatcher.find()) {           //对比 (同比、环比、年初等)
                List<String> cell = new ArrayList<>();
                try {
                    cell = ExcelCommon.splitCell(dataSource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cellList.addAll(cell);
//                log.info("对比---" + dataSource);
            } else if (mathMatcher.find()) {            //数学运算
                List<String> cell = new ArrayList<>();
                try {
                    cell = ExcelCommon.splitCell(dataSource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cellList.addAll(cell);
//                log.info("运算---" + dataSource);
            } else if (singletMatcher.find()) {         //单个单元格取值
                cellList.add(dataSource);
//                log.info("单个---" + dataSource);
            } else {
                log.info("数据来源错误：" + dataSource);
            }
        }
        return cellList;
    }

    @Override
    public List<Data> getData(List<DataInfo> dataInfos, Map<String, String> cells, Bank bank,String time) throws Exception {
        String math = "[+\\-*/]";              //匹配加减乘除运算
        String contrast = "^tb|^hb|^nc|^nczzl|^tqs|^sqs|^ncs";        //匹配同比、环比、年初
        String single = "^[a-zA-Z]?\\d+$";       //匹配单个单元格

        List<Data> dataList = new ArrayList<>();

        for(DataInfo dataInfo:dataInfos){

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

//          data.setExcelName();
//          data.setExcelSourceId();
//          data.setDepId();
//          data.setDepName();

            String dataSource = dataInfo.getDataSource().replace(" ","");

            Pattern patternMath = Pattern.compile(math);
            Matcher mathMatcher = patternMath.matcher(dataSource);

            Pattern patternContrast = Pattern.compile(contrast);
            Matcher contrastMatcher = patternContrast.matcher(dataSource);

            Pattern patternSingle = Pattern.compile(single);
            Matcher singletMatcher = patternSingle.matcher(dataSource);

            if (contrastMatcher.find()) {        //对比 (同比、环比、年初等)
//                log.info("对比---" + dataSource);
                String[] str =dataSource.split(",");
                if(str.length != 2){
                    log.info("数据来源错误：" + dataSource);
                    continue;
                }
//                log.info("#####:"+str[0]+","+str[1]);

                String suanshi = str[1];
                double nowValue = 0;    //当期数据
                Matcher sMathMatcher = patternMath.matcher(suanshi);
                Matcher sSingletMatcher = patternSingle.matcher(suanshi);
                if (sMathMatcher.find()) {
                    List<String> cellList = new ArrayList<>();
                    try {
                        cellList = ExcelCommon.splitCellStr(suanshi);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    log.info("begin:"+String.join("", cellList));
                    for(int i = 0;i<cellList.size();i++) {
                        Matcher cellMatcher = patternSingle.matcher(cellList.get(i));
                        if (cellMatcher.find()) {
                            cellList.set(i, cells.get(cellList.get(i)));
                        }
                    }

                    String sStr = String.join("", cellList);
                    try {
                        nowValue = MathUtil.jisuanStr(sStr);
                        log.info("jisuan:"+sStr+"value:"+nowValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(sSingletMatcher.find()){
//                    log.info(cells.get(str[1])+","+str[1]);
                    if(StringUtils.isNotEmpty(cells.get(str[1]))){
                        nowValue = Double.valueOf(cells.get(str[1]));
                    }
                }else{
                    log.info("数据来源错误：" + dataInfo.getDataSource());
                    continue;
                }


                switch (str[0]){
                    case "tqs":      //计算同比
                        String lastYear = DateUtils.getYearOnYear(time);
                        Data lastYearData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),lastYear);
                        double lValue = 0;
                        if(null != lastYearData){
                            if(StringUtils.isNotEmpty(lastYearData.getDataValue())){
                                lValue = Double.valueOf(lastYearData.getDataValue());
                            }
                        }
//                        log.info("同比："+dataSource+",当期："+nowValue+"，上年："+t);
                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(lValue)));
                        break;
                    case "sqs":      //计算环比
                        String last = dataInfo.getDataType().equals("季报")?DateUtils.getLastQuarter(time):DateUtils.getLastMonth(time);
//                        log.info("QVB:"+dataInfo.getDataType()+","+time+","+last);
                        Data lastMonthData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),last);
                        double sValue = 0;
                        if(null != lastMonthData){
                            if(StringUtils.isNotEmpty(lastMonthData.getDataValue())){
                                sValue = Double.valueOf(lastMonthData.getDataValue());
                            }
                        }
//                        log.info("环比："+dataSource+",当期："+nowValue+"，上期："+tt);
                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(sValue)));
                        break;
                    case "ncs":      //计算年初
                        String yearFirst = DateUtils.getYearFirstMonth(time);
                        Data yearFirstData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),yearFirst);
                        double nValue = 0;
                        if(null != yearFirstData){
                            if(StringUtils.isNotEmpty(yearFirstData.getDataValue())){
                                nValue = Double.valueOf(yearFirstData.getDataValue());
                            }
                        }
//                        log.info("环比："+dataSource)+",当期："+nowValue+"，上期："+ttt);
                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(nValue)));
                        break;
                    case "nczzl":
                        String nc = DateUtils.getYearFirstMonth(time);
                        Data ncData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),nc);
                        double ovalue = 0;
                        if(null != ncData){
                            if(StringUtils.isNotEmpty(ncData.getDataValue())){
                                Double ncValue = Double.valueOf(ncData.getDataValue());
                                if(ncValue!=0) {
                                    ovalue = (nowValue - ncValue) / ncValue;
                                }else{
                                    throw new Exception("年初无值！");
                                }
                            }
                        }

//                      log.info("环比："+dataSource)+",当期："+nowValue+"，上期："+tttt);
                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(ovalue)));
                        break;
                    case "tb":
//                        String lastYear = DateUtils.getYearOnYear(time);
//                        Data lastYearData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),lastYear);
//                        double t = 0;
//                        if(null != lastYearData){
//                            if(StringUtils.isNotEmpty(lastYearData.getDataValue())){
//                                Double lValue = Double.valueOf(lastYearData.getDataValue());
//                                t = lValue;
//                                value = nowValue - lValue;
//                            }
//                        }
////                        log.info("同比："+dataSource+",当期："+nowValue+"，上年："+t);
//                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(value)));
                        break;
                    case "hb":
//                        String last = dataInfo.getDataType().equals("季报")?DateUtils.getLastQuarter(time):DateUtils.getLastMonth(time);
////                        log.info("QVB:"+dataInfo.getDataType()+","+time+","+last);
//                        Data lastMonthData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),last);
//                        double tt = 0;
//                        if(null != lastMonthData){
//                            if(StringUtils.isNotEmpty(lastMonthData.getDataValue())){
//                                Double lValue = Double.valueOf(lastMonthData.getDataValue());
//                                value = nowValue - lValue;
//                                tt = lValue;
//                            }
//                        }
////                        log.info("环比："+dataSource+",当期："+nowValue+"，上期："+tt);
//                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(value)));
                        break;
                    case "nc":
//                        String yearFirst = DateUtils.getYearFirst(time);
//                        Data yearFirstData = dataService.getByBankIdOnlyCodeAndTime(bank.getId(),dataInfo.getOnlyCode(),yearFirst);
//                        double ttt = 0;
//                        if(null != yearFirstData){
//                            if(StringUtils.isNotEmpty(yearFirstData.getDataValue())){
//                                Double lValue = Double.valueOf(yearFirstData.getDataValue());
//                                value = nowValue - lValue;
//                                ttt = lValue;
//                            }
//                        }
////                        log.info("环比："+dataSource)+",当期："+nowValue+"，上期："+ttt);
//                        data.setDataValue(MathUtil.liangweixiaoshu(String.valueOf(value)));
                        break;
                    default :
                        log.info("数据来源错误：" + dataInfo.getDataSource());
                        continue;
                }

            } else if (mathMatcher.find()) {        //数学运算
                List<String> cellList = new ArrayList<>();
                try {
                    cellList = ExcelCommon.splitCellStr(dataInfo.getDataSource());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                log.info("begin:"+String.join("", cellList));
                for(int i = 0;i<cellList.size();i++) {
                    Matcher cellMatcher = patternSingle.matcher(cellList.get(i));
                    if (cellMatcher.find()) {
                        cellList.set(i, cells.get(cellList.get(i)));
                    }
                }

                String str = String.join("", cellList);
                double value = 0;
                try {
                    value = MathUtil.jisuanStr(str);
                    log.info("jisuan:"+str+"value:"+value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                data.setDataValue(String.valueOf(value));
//                log.info("运算---" + dataInfo.getDataSource());
            } else if (singletMatcher.find()) {     //单个单元格取值
                data.setDataValue(cells.get(dataInfo.getDataSource()));
//                log.info("单个---" + dataInfo.getDataSource());
            } else {
//                cellList.add(dataInfo.getDataSource());
                log.info("数据来源错误：" + dataInfo.getDataSource());
                continue;
            }
            dataList.add(data);
        }
        return dataList;
    }

    @Override
    public void insertBatchList(List<DataInfo> dataInfoList) {
        List<DataInfo> updateList = new ArrayList<DataInfo>();
        List<DataInfo> insertList = new ArrayList<DataInfo>();
        for(DataInfo list: dataInfoList){
            DataInfo dataInfo= this.dataInfoMapper.selectByOnlyCodeAndGroupId(list.getOnlyCode(),list.getGroupId());
            if(null!=dataInfo){
                list.setId(dataInfo.getId());
                updateList.add(list);
            }else{
                insertList.add(list);
            }
        }
        if(updateList.size()>0){
            this.dataInfoMapper.updates(updateList);
        }
        if(insertList.size()>0){
            this.dataInfoMapper.insertBatchList(insertList);
        }
    }

    @Override
    public int updates(List<DataInfo> item) {
        return this.dataInfoMapper.updates(item);
    }

    @Override
    public ArrayList<DataInfo> dataInfoListPage() {
        return dataInfoMapper.dataInfoListPage ();
    }
    @Override
    public int updateDataInfo(DataInfo dataInfo) {
        return dataInfoMapper.updateByPrimaryKeySelective (dataInfo);
    }

    @Override
    public int insertSelective(DataInfo dataInfo) {
        return dataInfoMapper.insertSelective (dataInfo);
    }

    @Override
    public int deleteById(int id) {
        return dataInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteAllByOnlyCode(String code) {
        return dataInfoMapper.deleteAllByOnlyCode(code);
    }

    @Override
    public List<DataInfo> selectAllExcelCode(String excelCode, Integer groupId , String param2) {
        return dataInfoMapper.selectAllExcelCode(excelCode ,groupId ,param2);
    }

    @Override
    public DataInfo selectOrderByMaxId(Integer groupId) {
        return dataInfoMapper.selectOrderByMaxId (groupId);
    }

    //根据onlyCodes   得到规则集合
    @Override
    public List<DataInfo> toViewRulesByOnlyCodes(String onlyCodes) {
        return dataInfoMapper.toViewRulesByOnlyCodes (onlyCodes);
    }

    @Override
    public List<DataInfo> toViewRulesByGroupIdDataNames(Integer groupId, String dataNames, String parent) {
        return dataInfoMapper.toViewRulesByGroupIdDataNames (groupId,dataNames,parent);
    }

    @Override
    public List<DataInfo> getAllOnlyCode() {
        return dataInfoMapper.selectAllOnlyCode();
    }


}
