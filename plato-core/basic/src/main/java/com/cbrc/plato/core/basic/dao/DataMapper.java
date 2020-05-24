package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.Data;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DataDAO继承基类
 */
@Repository
public interface DataMapper{

    int updateByPrimaryKeySelective(Data record);

    int updates(List<Data> item);

    int inserts(List<Data> item);

    int insertSelective(Data record);

    Data selectByBankIdOnlyCodeAndTime(int bankId,String onlyCode,String time);

    List<Data> selectChildrenByParentAndBankTime(String time,int bankId,String parent);

    List<Data> selectQXGLByBankAndTimes(String times,int bankId);

    List<Data> selectByBankAndTimes(String times,int bankId);

    List<Data> selectZCJGDataByBankAndTime(String time, int bankId, String codes);

    List<Data> selectYQ90TByBankAndTimes(String times,int bankId);

//    <!--资产结构的后置条件-本期新增不良贷款占新发放贷款的比例-显示正常类（正常 + 关注）降级为不良贷款的迁徙率-显示处置不良贷款占新形成不良贷款的比例-->
    List<Data> selectBuLiangDaiKuanHZTJByBankAndTimes(String times,int bankId,String stringShuJuXiang );

    List<Data> selectBWYWDataByBankAndTimeCode(String times,int bankId,String code );

    List<Integer> selectAllBank();

    List<String> selectAllTime();

    List<String> selectRecentTime();

    // 表外业务后置条件 时间段 parent bankId
    List<Data> selectChildrenByParentAndBankTimes(String times,int bankId,String parent);
}
