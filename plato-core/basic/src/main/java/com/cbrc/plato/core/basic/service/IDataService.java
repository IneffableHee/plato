package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.Data;
import com.cbrc.plato.util.datarule.report.ReportTable;

import java.util.List;

public interface IDataService {

    void updates(List<Data> excelMoulds);

    List<Data> getQXGLDataByBankAndTimes(List<String> times,int bankId);

    List<Data> getDataByBankAndTimes(List<String> times,int bankId);

    List<Data> getChildrenByParentAndBankTime(String time, int bankId,String parent);

    List<Data> getZCJGDataByBankAndTime(String time,int bankId,String codes);

    Data getByBankIdOnlyCodeAndTime(int bankId,String onlyCode,String time);

    List<Integer> getBanks();

    List<String> getTimes();

    List<Data> selectYQ90TByBankAndTimes(List<String> times,int bankId);

    //    <!--资产结构的后置条件-本期新增不良贷款占新发放贷款的比例-显示正常类（正常 + 关注）降级为不良贷款的迁徙率-显示处置不良贷款占新形成不良贷款的比例-->
    List<Data> selectBuLiangDaiKuanHZTJByBankAndTimes(List<String> times,int bankId,List<String> stringShuJuXiang);

    List<Data>   selectBWYWDataByBankAndTimeCode (List<String> times,int bankId,String code);

    // 表外业务后置条件 时间段 parent bankId
    List<Data> getChildrenByParentAndBankTimes(List<String> times,int bankId,String parent);

    List<Data> getData(List<Data> dataList, ReportTable reportTable);
}
