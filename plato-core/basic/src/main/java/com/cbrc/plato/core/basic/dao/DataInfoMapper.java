package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.BankGroup;
import com.cbrc.plato.core.basic.model.DataInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * DataInfoMapper继承基类
 */
@Repository
public interface DataInfoMapper {
    List<DataInfo> selectAllDataInfo(@Param("roleId")Integer roleId);

    List<DataInfo> dataInfoListPageByCode(@Param("dataName")String dataName,@Param("groupId")Integer groupId,@Param("roleId")Integer roleId);

    List<BankGroup> selectBankGroupList(@Param("roleId")Integer roleId);

    List<DataInfo> selectMonthDataInfo(@Param("roleId")Integer roleId);

    List<DataInfo> getDataInfoListByGroupId(@Param("groupId")int groupId,@Param("roleId")int roleId);

    List<DataInfo> getMonthDataInfoListByGroupId(@Param("groupId")int groupId,@Param("roleId")int roleId);

    List<DataInfo> selectByGroupId(int gid);

    DataInfo selectByOnlyCodeAndGroupId(String onlyCode,int gid);

    DataInfo selectById(int id);

    DataInfo selectByOnlyCode(String onlyCode);

    List<DataInfo> getDataInfoByQuanXia(int gid);

    int deleteByPrimaryKey(Integer id);

    int deleteAllByOnlyCode(String code);

    int insertBatchList(List<DataInfo> dataInfoList);

    int updates(List<DataInfo> item);

    int updateByPrimaryKeySelective(DataInfo dataInfo);
    // dataInfo 分页查询
    ArrayList dataInfoListPage();

    // insertSelective
    int insertSelective(DataInfo dataInfo);

    List<DataInfo> selectAllExcelCode(String excelCode , Integer groupId , String param2);

    //根据groupId分组排序取Id最大的
    DataInfo  selectOrderByMaxId(Integer groupId);

    //<!--   // 向页面返回取数规则 -->
    List<DataInfo> toViewRulesByOnlyCodes( @Param(value="onlyCodes") String onlyCodes);


    //<!--   // 向页面返回取数规则 2-->
    List<DataInfo> toViewRulesByGroupIdDataNames( @Param(value="groupId") Integer groupId,@Param(value="dataNames") String dataNames,@Param(value="parent") String parent);

    List<DataInfo> selectAllOnlyCode();
}
