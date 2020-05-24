package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.DataMapper;
import com.cbrc.plato.core.basic.model.Data;
import com.cbrc.plato.core.basic.service.IDataService;
import com.cbrc.plato.util.datarule.report.ReportCell;
import com.cbrc.plato.util.datarule.report.ReportTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataServiceImpl implements IDataService {

    @Autowired
    DataMapper  dataMapper;

    @Override
    public void updates(List<Data> dataList) {
        //List<Data> updateList = new ArrayList<>();
        //List<Data> insertList = new ArrayList<>();
        /*for(Data data:dataList){
//            log.info("ExcelFileListServiceImpl updates"+data.getDataValue()+","+data.getBankId()+","+data.getOnlyCode()+","+data.getDataTime());
            Data oldData = this.dataMapper.selectByBankIdOnlyCodeAndTime(data.getBankId(),data.getOnlyCode(),data.getDataTime());
            if(null!=oldData){
                if(StringUtil.isNotEmpty(oldData.getDataValue())&&oldData.getDataValue().equals(data.getDataValue())){
                    continue;
                }else if(StringUtil.isEmpty(oldData.getDataValue())&&StringUtil.isEmpty(data.getDataValue())) {
                    continue;
                }else{
                    data.setDataId(oldData.getDataId());
                    updateList.add(data);
                }
            }else {
                insertList.add(data);
            }
        }*/
        //long endsql = System.currentTimeMillis();
        //log.info("程序查询数据运行时间：" + (endsql - startsql) + "ms,");
        /*if(insertList.size()>0){
            log.info("insert :"+insertList.size());
            this.dataMapper.inserts(insertList);
        }*/
        long startsql = System.currentTimeMillis();
        this.dataMapper.inserts(dataList);
        long endsql11 = System.currentTimeMillis();
        log.info("程序insert数据运行时间：" + (endsql11 - startsql) + "ms,");
        /*if(updateList.size()>0){
            log.info("update :"+updateList.size());
            this.dataMapper.updates(updateList);
        }*/
        //long endsql22 = System.currentTimeMillis();
        //log.info("程序update数据运行时间：" + (endsql22 - endsql11) + "ms,");
    }


    @Override
    public List<Data> getQXGLDataByBankAndTimes(List<String> times, int bankId) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        System.out.println(time);
        return dataMapper.selectQXGLByBankAndTimes(time,bankId);
    }

    @Override
    public List<Data> getDataByBankAndTimes(List<String> times, int bankId) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        return dataMapper.selectByBankAndTimes(time,bankId);
    }

    @Override
    public List<Data> getChildrenByParentAndBankTime(String time, int bankId,String parent) {
        return dataMapper.selectChildrenByParentAndBankTime(time,bankId,parent);
    }

    @Override
    public List<Data> getZCJGDataByBankAndTime(String time, int bankId, String codes) {
        return dataMapper.selectZCJGDataByBankAndTime(time,bankId,codes);
    }

    @Override
    public Data getByBankIdOnlyCodeAndTime(int bankId, String onlyCode, String time) {
        return this.dataMapper.selectByBankIdOnlyCodeAndTime(bankId,onlyCode,time);
    }

    @Override
    public List<Integer> getBanks() {
        return dataMapper.selectAllBank();
    }

    @Override
    public List<String> getTimes() {
        return dataMapper.selectAllTime();
    }

    @Override
    public List<Data> selectYQ90TByBankAndTimes(List<String> times, int bankId) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        return dataMapper.selectYQ90TByBankAndTimes (time,bankId);
    }

    //    <!--资产结构的后置条件-本期新增不良贷款占新发放贷款的比例-显示正常类（正常 + 关注）降级为不良贷款的迁徙率-显示处置不良贷款占新形成不良贷款的比例-->
    @Override
    public List<Data> selectBuLiangDaiKuanHZTJByBankAndTimes(List<String> times, int bankId, List<String> stringShuJuXiang) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        String result="'"+ StringUtils.join(stringShuJuXiang,"','")+"'";
        return dataMapper.selectBuLiangDaiKuanHZTJByBankAndTimes (time,bankId,result);
    }

    @Override
    public List<Data> selectBWYWDataByBankAndTimeCode(List<String> times, int bankId, String stringOnlyCode) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        String code = "'"+stringOnlyCode+"'";
        return dataMapper.selectBWYWDataByBankAndTimeCode (time,bankId,code);
    }

    // 表外业务后置条件 时间段 parent bankId
    @Override
    public List<Data> getChildrenByParentAndBankTimes(List<String> times, int bankId, String parent) {
        String time = "'"+ StringUtils.join(times,"','")+"'";
        return dataMapper.selectChildrenByParentAndBankTimes (time,bankId,parent);
    }

    @Override
    public List<Data> getData(List<Data> dataList, ReportTable reportTable) {
        for (Data data:dataList){
            System.out.println(data.getBankName()+"-"+data.getDataRule()+":"+data.getDataValue());
            for(ReportCell reportCell:reportTable.getReportCells()){
                if(data.getBankId()==reportCell.getBankId()&&data.getExcelCode().equals(reportCell.getCell())&&data.getDataRule().equals(reportCell.getTatalSource())){
                    if(reportCell.getValue()!=null){
                        System.out.println(data.getBankId()+"&"+reportCell.getBankId()+"&"+data.getExcelCode()+"&"+reportCell.getCell()+"&"+data.getDataRule()+"&"+reportCell.getTatalSource()+":"+reportCell.getValue());
                        data.setDataValue(reportCell.getValue());
                    }
                }
            }
        }
        return dataList;
    }


}
