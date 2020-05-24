package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.BankGroup;
import com.cbrc.plato.core.basic.model.Data;
import com.cbrc.plato.core.basic.model.DataInfo;
import com.cbrc.plato.util.datarule.report.SourceMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDataInfoService {
    List<DataInfo> getDataInfoList(Integer roleId);

    List<DataInfo> getDataInfoListByGroupId(int groupId , int roleId);

    List<BankGroup> selectBankGroupList(Integer roleId);

    List<DataInfo> dataInfoListPageByCode(String dataName,Integer groupId , Integer roleId);

    List<DataInfo> getMonthDataInfoList(Integer roleId);

    List<DataInfo> getMonthDataInfoListByGroupId(int groupId , int roleId);

    DataInfo getByOnlyCodeAndGroupId(String onlyCode,int gid);

    DataInfo getById(int id);

    DataInfo selectByOnlyCode(String onlyCode);

    List<DataInfo> getDataInfoByGroupId(int gid);

    Set<String> getCellSet(List<DataInfo> dataInfos) throws Exception;

    List<Data> getData(List<DataInfo> dataInfos, Map<String,String> cells, Bank bank, String time) throws Exception;

    void insertBatchList(List<DataInfo> dataInfoList);

    int updates(List<DataInfo> item);
    // DataInfo  分页查询
    ArrayList<DataInfo> dataInfoListPage();

    int updateDataInfo(DataInfo dataInfo);

    int insertSelective(DataInfo dataInfo);

    int deleteById(int id);

    int deleteAllByOnlyCode(String code);   //根据onlycode删除本身及后置条件

    List<DataInfo> selectAllExcelCode(String excelCode , Integer groupId , String param2);

    //根据groupId分组排序取Id最大的
    DataInfo  selectOrderByMaxId(Integer groupId);

    SourceMatrix getSourceMatrix(SourceMatrix sourceMatrix) throws Exception;

    List<DataInfo> toViewRulesByOnlyCodes(String onlyCodes);

    List<DataInfo> toViewRulesByGroupIdDataNames(Integer groupId, String dataNames, String parent);

    List<DataInfo> getAllOnlyCode();
}
