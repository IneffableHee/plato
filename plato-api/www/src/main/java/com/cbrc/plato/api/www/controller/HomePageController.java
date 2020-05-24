package com.cbrc.plato.api.www.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.Data;
import com.cbrc.plato.core.basic.model.DataInfo;
import com.cbrc.plato.core.basic.model.QnyjYuqi90East;
import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import com.cbrc.plato.util.math.MathUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/home")
public class HomePageController {
    @Autowired
    IDataService dataService;

    @Autowired
    IDataInfoService dataInfoService;

    @Autowired
    IBankService bankService;

   @Autowired
    IDataJZDService dataJZDService;

   @Autowired
    IQnyjYuqi90EastService  qnyjYuqi90EastService;

    @RequestMapping("/getQXGL")
    public PlatoBasicResult getQXGL(String startTime,String endTime,String bankName){
//        List<String> timeList1 = DateUtils.getTimeList(startTime,endTime);
        List<String> timeList = DateUtils.getRecentMouth (endTime,12);//20191010更改（数据为12个月的）
        if(timeList == null || timeList.size()==0)
            return PlatoResult.failResult("时间段不正确");
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList= dataService.getQXGLDataByBankAndTimes(timeList,bank.getId());
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String[] DateValue = timeList.toArray(new String[timeList.size()]);
        String[] cunkuanValue=new String[timeList.size()];
        String[] daikuanValue=new String[timeList.size()];
        String[] zichanValue=new String[timeList.size()];
        String[] fuzhaiValue=new String[timeList.size()];
        String[] bulianglvValue=new String[timeList.size()];
        for(int i=0;i<timeList.size();i++){
            String cunkuan=null,daikuan=null,zichan=null,fuzhai=null,bulianglv=null;
            for(Data data:dataList){
                if(data.getDataTime().equals(timeList.get(i))){
                    switch (data.getDataName()){
                        case "存款":
                            cunkuan=MathUtil.wanyuanToyi(  MathUtil.isNanInfinity(data.getDataValue()));
                            break;
                        case "贷款":
                                daikuan = MathUtil.wanyuanToyi(  MathUtil.isNanInfinity(data.getDataValue()));
                            break;
                        case "资产":
                                zichan = MathUtil.wanyuanToyi(  MathUtil.isNanInfinity(data.getDataValue()));
                            break;
                        case "负债":
                                fuzhai = MathUtil.wanyuanToyi(  MathUtil.isNanInfinity(data.getDataValue()));
                            break;
                        case "不良率":
                                bulianglv =  MathUtil.isNanInfinity(data.getDataValue());
                            break;
                    }
                }
            }
            cunkuanValue[i]=cunkuan;
            daikuanValue[i]=daikuan;
            zichanValue[i]=zichan;
            fuzhaiValue[i]=fuzhai;
            bulianglvValue[i]=bulianglv;
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("存款", cunkuanValue);
        ((JSONObject) json).put("贷款", daikuanValue);
        ((JSONObject) json).put("资产", zichanValue);
        ((JSONObject) json).put("负债", fuzhaiValue);
        ((JSONObject) json).put("不良率", bulianglvValue);
        ((JSONObject) json).put("日期", DateValue);
        // 20191015 新增加功能  查看取数规则
        List<String > stringList =  new ArrayList<>(Arrays.asList("存款","贷款","资产","负债","不良率"));
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,null) );
        return PlatoResult.successResult(json);
    }

    @RequestMapping("/qxgl/cunkuan")
    public PlatoBasicResult getQXGLCunKuan(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        String parentCode = null;
        String[] strArray = {"单位存款","个人存款","国库定期存款","临时性存款","其他存款"};
        if(bank.getGroupId() ==8){
            parentCode = "A001";
        }else if(bank.getGroupId() == 9){
            parentCode="M001";
        }else if(bank.getGroupId() == 10){
            parentCode="C001";
        }else if(bank.getGroupId() == 11){
            parentCode="D001";
            strArray = new String[]{"单位存款","个人存款"};
        }else if(bank.getGroupId() == 12){
            parentCode="E001";
            strArray = new String[]{"单位存款","单位活期存款","单位定期存款","个人存款","个人活期存款","个人定期存款"};
        }else if(bank.getGroupId() == 13){
            parentCode = "F001";
        }else if(bank.getGroupId() == 14){
            parentCode = "G001";
        }else if(bank.getGroupId() == 2){
            parentCode = "L001";
        }else if(bank.getGroupId() == 7){
            parentCode="H001";
            strArray = new String[]{"单位存款","单位活期存款","单位定期存款","个人存款","个人活期存款","个人定期存款"};
        }else if(bank.getGroupId() == 4){
            parentCode="K001";
            strArray = new String[]{"单位存款","个人存款"};
        }
        dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),parentCode);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");

        for(Data data:dataList){
                data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
        JSON json = new JSONObject();
       List<String> stringList = new ArrayList<>(Arrays.asList(strArray));
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,parentCode));
        ((JSONObject) json).put ("dataList",dataList );
        ((JSONObject) json).put ("xAxis",strArray );
        return PlatoResult.successResult(json);
    }

    @RequestMapping("/qxgl/daikuan")
    public PlatoBasicResult getQXGLDaiKuan(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        String parentCode = null;
        String[] strArray = {"短期贷款", "中长期贷款", "贴现及买断式转贴现", "贸易融资", "融资租赁", "各项垫款"};
        if(bank.getGroupId() ==8){
            parentCode="A002";
        }else if(bank.getGroupId() == 9){
            parentCode="M002";
        }else if(bank.getGroupId() == 10){
            parentCode="C002";
        }else if(bank.getGroupId() == 11){
            parentCode="D002";
            strArray = new String[]{"短期贷款","中长期贷款"};
        }else if(bank.getGroupId() == 12){
            parentCode = "E002";
        }else if(bank.getGroupId() == 13){
            parentCode = "F002";
        }else if(bank.getGroupId() == 14){
            parentCode = "G002";
        }else if(bank.getGroupId() == 2){
            parentCode ="L002";
        }else if(bank.getGroupId() == 7){
            parentCode = "H002";
        }else if(bank.getGroupId() == 4){
            parentCode ="K002";
            strArray = new String[]{"短期贷款","中长期贷款"};
        }
        dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),parentCode);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        for(Data data:dataList){
                data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
        JSON json = new JSONObject();
        List<String> stringList = new ArrayList<>(Arrays.asList(strArray ));
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,parentCode));
        ((JSONObject) json).put ("dataList",dataList );
        ((JSONObject) json).put ("axisX",stringList );
        return PlatoResult.successResult(json);
    }
    @RequestMapping("/qxgl/zichan")
    public PlatoBasicResult getQXGLZiChan(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        String parentCode = null;
        String[] strArray = {"现金","存放中央银行款项","存放同业款项","存放系统内款项","应收利息","贷款","贸易融资","贴现及买断式转贴现","投资"};
        if(bank.getGroupId() ==8){
            parentCode="A003";
        }else if(bank.getGroupId() == 9){
            parentCode="M003";
            strArray = new String[] {"现金","存放中央银行款项","存放同业款项","存放系统内款项","应收利息","贷款","贸易融资","贴现及买断式转贴现"};
        }else if(bank.getGroupId() == 10){
            parentCode="C003";
        }else if(bank.getGroupId() == 11){
            parentCode="D003";
            strArray = new String[]{"现金","存放中央银行款项","存放同业款项","贷款"};
        }else if(bank.getGroupId() == 12){
            parentCode="E003";
            strArray = new String[]{"现金","存放中央银行款项","贷款","存放系统内款项","各项资产减值损失准备"};
        }else if(bank.getGroupId() == 13){
            parentCode="F003";
        }else if(bank.getGroupId() == 14){
            parentCode="G003";
        }else if(bank.getGroupId() == 2){
            parentCode="L003";
        }else if(bank.getGroupId() == 7){
            parentCode="H003";
            strArray = new String[]{"现金","存放中央银行款项","贷款","存放系统内款项","各项资产减值损失准备"};
        }else if(bank.getGroupId() == 4){
            parentCode="K003";
            strArray = new String[]{"现金","存放中央银行款项","存放同业款项","贷款"};
        }
        dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),parentCode);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        for(Data data:dataList){
                data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
        JSON json = new JSONObject();
        List<String> stringList = new ArrayList<>(Arrays.asList( strArray));
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,parentCode));
        ((JSONObject) json).put ("dataList",dataList );
        ((JSONObject) json).put ("axisX",stringList );
        return PlatoResult.successResult(json);
    }
    @RequestMapping("/qxgl/fuzhai")
    public PlatoBasicResult getQXGLFuZhai(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        String parentCode = null;
        String[] strArray = {"单位存款", "储蓄存款", "向中央银行借款", "同业存放款项", "同业拆入", "应付利息"};
        if(bank.getGroupId() ==8){
            parentCode="A004";
        }else if(bank.getGroupId() == 9){
            parentCode="M001";
            strArray = new String[]{"单位存款", "个人存款", "国库定期存款", "临时性存款", "其他存款"};
        }else if(bank.getGroupId() == 10){
            parentCode="C001";
            strArray = new String[]{"单位存款", "个人存款", "国库定期存款", "临时性存款", "其他存款"};
        }else if(bank.getGroupId() == 11){
            parentCode="D004";
            strArray = new String[]{"单位存款", "储蓄存款", "向中央银行借款", "同业存放款项"};
        }else if(bank.getGroupId() == 12){
            parentCode="E004";
            strArray = new String[]{"单位存款","储蓄存款","同业存放款项","存入保证金"};
        }else if(bank.getGroupId() == 13){
            parentCode="F001";
            strArray = new String[]{"单位存款", "个人存款", "国库定期存款", "临时性存款", "其他存款"};
        }else if(bank.getGroupId() == 14){
            parentCode="G001";
            strArray = new String[]{"单位存款", "个人存款", "国库定期存款", "临时性存款", "其他存款"};
        }else if(bank.getGroupId() == 2){
            parentCode="L001";
            strArray = new String[]{"单位存款", "个人存款", "国库定期存款", "临时性存款", "其他存款"};
        }else if(bank.getGroupId() == 7){
            parentCode="H004";
            strArray = new String[]{"单位存款","储蓄存款","同业存放款项","存入保证金"};
        }else if(bank.getGroupId() == 4){
            parentCode="K004";
            strArray = new String[]{"单位存款", "储蓄存款", "向中央银行借款", "同业存放款项"};
        }
        dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),parentCode);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        for(Data data:dataList){
                data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
        JSON json = new JSONObject();
        List<String> stringList = null;
        stringList = new ArrayList<>(Arrays.asList(strArray));

        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,parentCode));
        ((JSONObject) json).put ("dataList",dataList );
        ((JSONObject) json).put ("axisX",stringList );
        return PlatoResult.successResult(json);
    }
    @RequestMapping("/qxgl/bulianglv")
    public PlatoBasicResult getQXGLBuLiang(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        if(bank.getGroupId() ==8){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"A005");
        }else if(bank.getGroupId() == 12){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"E005");
        }else if(bank.getGroupId() == 13){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"F005");
        }else if(bank.getGroupId() == 14){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"G005");
        }else if(bank.getGroupId() == 2){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"L005");
        }else if(bank.getGroupId() == 7){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"H005");
        }else if(bank.getGroupId() == 4){
            dataList = dataService.getChildrenByParentAndBankTime(time,bank.getId(),"K005");
        }
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");

        for(Data data:dataList){
            data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        return PlatoResult.successResult(dataList);
    }

    /*资产结构（五级分类）*/
    @RequestMapping("/zcjg")
    public PlatoBasicResult getZCJG(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList = null;
        String onlyCodes = null;
        if(bank.getGroupId() ==8){
            onlyCodes= "'A107','A108','A109','A110','A111'";
        }else if(bank.getGroupId() == 12){
            onlyCodes= "'E111','E112','E113','E114','E115'";
        }else if(bank.getGroupId() == 13){
            onlyCodes= "'F141','F142','F143','F144','F145'";
        }else if(bank.getGroupId() == 14){
            onlyCodes ="'G134','G135','G136','G137','G138'";
        }else if(bank.getGroupId() == 4){
            onlyCodes="'K100','K101','K097','K098','K099'";//20191015  更改 徐文凯'K095','K096','K097','K098','K099'
        }else if(bank.getGroupId() == 7){
            onlyCodes="'H110','H111','H112','H113','H114'";
        }else if(bank.getGroupId () == 15){
            onlyCodes="'H110','H111','H112','H113','H114'";
        }else if(bank.getGroupId () == 2){
            onlyCodes="'L0134','L0135','L0136','L0137','L0138'";
        }else if(bank.getGroupId () == 9){
            onlyCodes="'M0134','M0135','M0136','M0137','M0138'";
        }else if(bank.getGroupId() == 6){
            onlyCodes = "'I062','I063','I064','I065','I066'"; //农发行
        }else if(bank.getGroupId() ==10){
            onlyCodes ="'C141','C142','C143','C144','C145'"; //农合汇总
        } else if(bank.getGroupId() == 11){
            onlyCodes = "'D095','D096','D097','D098','D099'";//村镇银行汇总
        }
        dataList = dataService.getZCJGDataByBankAndTime(time,bank.getId(),onlyCodes);
//        else if(bank.getGroupId () == 15){
//            dataList = dataService.getZCJGDataByBankAndTime(time,bank.getId(), "  ");
//        }
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        for(Data data:dataList){
            data.setDataValue( MathUtil.isNanInfinity(data.getDataValue()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
         JSON json = new JSONObject();
        ((JSONObject) json).put ("dataRules",toViewRulesByOnlyCodes(onlyCodes));
        ((JSONObject) json).put ("dataList",dataList );
        return PlatoResult.successResult(json);
    }
    /*贷款损失准备充足率,农合、村镇银行*/
    @RequestMapping("dksszb")
    public PlatoBasicResult getDKSSZB(String time,String bankName) throws Exception{
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }

        if(bank.getGroupId() != 11 && bank.getGroupId() != 10 && bank.getGroupId() != 4 && bank.getGroupId() != 13 && bank.getGroupId() != 14){
            return PlatoResult.failResult("该机构不存在此项数据");
        }

        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        Map<String,String> map=DateUtils.getRecentQuarter(time,5);
        for(Map.Entry<String, String> entry : map.entrySet()){
            sessionList.add(entry.getKey());
            dateList.add(entry.getValue()) ;
        }
        String[] dateValues = arrayReverse(sessionList.toArray(new String[sessionList.size()]));//20191010  更改  倒叙为201806 201809 201812 201903
        String[] dataValues=new String[dateList.size()];
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.getDataByBankAndTimes(dateList,bank.getId());
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String onlyCode = null;
        if(bank.getGroupId() == 11 || bank.getGroupId() == 10 || bank.getGroupId() == 13 || bank.getGroupId() ==14 || bank.getGroupId() == 4){      //农合
            if(bank.getGroupId() == 13){
                onlyCode = "F110";
            }else if(bank.getGroupId() == 14){
                onlyCode = "G104";
            }else if(bank.getGroupId() == 4){
                onlyCode = "K072";
            }else if(bank.getGroupId() == 11){
                onlyCode = "K071";
            }else if(bank.getGroupId() == 10){
                onlyCode = "C110";
            }
            for(int i=0;i<dateList.size();i++){
                String dataValue=null;
                for(Data data:dataList){
                    if(data.getDataTime().equals(dateList.get(i))&&data.getOnlyCode().equals(onlyCode)){
                            dataValue = MathUtil.isNanInfinity(data.getDataValue());
                        break;
                    }
                }
                dataValues[i] = dataValue;
            }
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("date", dateValues);
        ((JSONObject) json).put("value", dataValues);
        ((JSONObject) json).put("dataRules",toViewRulesByOnlyCodes ("'"+onlyCode+"'"));
        return PlatoResult.successResult(json);
    }
    /*拨备覆盖率,农合、村镇银行*/
    @RequestMapping("bbfgl")
    public PlatoBasicResult getBBFGL(String time,String bankName) throws Exception{
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }

        if(bank.getGroupId() != 4 && bank.getGroupId() != 13 && bank.getGroupId() != 14){
            return PlatoResult.failResult("该机构不存在此项数据");
        }

        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        Map<String,String> map=DateUtils.getRecentQuarter(time,5);
        for(Map.Entry<String, String> entry : map.entrySet()){
            sessionList.add(entry.getKey());
            dateList.add(entry.getValue()) ;
        }
        String[] dateValues = arrayReverse (sessionList.toArray(new String[sessionList.size()]));//arrayReverse() 20191010  更改  倒叙为201806 201809 201812 201903
        String[] dataValues=new String[dateList.size()];
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.getDataByBankAndTimes(dateList,bank.getId());
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String onlyCode = null;
        if(bank.getGroupId() == 11||bank.getGroupId() == 13 || bank.getGroupId() ==14|| bank.getGroupId() ==4 || bank.getGroupId() ==10){      //13农商  14农合 4村镇
            if(bank.getGroupId() == 13){
                onlyCode = "F111";
            }else if(bank.getGroupId() == 14){
                onlyCode = "G105";
            }else if(bank.getGroupId() == 4){
                onlyCode = "K073";
            }else if(bank.getGroupId() == 11){//村镇汇总
                onlyCode = "K072";
            }else if(bank.getGroupId() == 10){//农合汇总
                onlyCode = "C111";
            }
            for(int i=0;i<dateList.size();i++){
                String dataValue=null;
                for(Data data:dataList){
                    if(data.getDataTime().equals(dateList.get(i))&&data.getOnlyCode().equals(onlyCode)){
                            dataValue =  MathUtil.isNanInfinity(data.getDataValue());
                        break;
                    }
                }
                dataValues[i] = dataValue;
            }
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("date", dateValues);
        ((JSONObject) json).put("value", dataValues);
        ((JSONObject) json).put("dataRules",toViewRulesByOnlyCodes ("'"+onlyCode+"'"));
        return PlatoResult.successResult(json);
    }
    @RequestMapping("/getBanks")
    public PlatoBasicResult getBanks(){
        List<Integer> bankIdList = dataService.getBanks();
        if(bankIdList==null){
            return PlatoResult.failResult("无机构权限");
        }
        List<String> bankList = new ArrayList<>();
        for(Integer i:bankIdList){
            Bank bank = bankService.getById(i);
            if(bank!=null)
                bankList.add(bank.getShortName());
        }
        return PlatoResult.successResult(bankList);
    }

    @RequestMapping("/getTimes")
    public PlatoBasicResult getTimes(){
        List<String> times = dataService.getTimes();
        if(times==null){
            return PlatoResult.failResult("无数据");
        }
        return PlatoResult.successResult(times);
    }
    /**
     * kai_w_x
     * @param
     * @param bankName
     * @return
     * 逾期90天以上的
     * @throws Exception
     */
    @RequestMapping("yq90t")
    public PlatoBasicResult getYQ90T(String startTime,String endTime,String bankName) throws Exception {
//        List<String> timeList = DateUtils.getTimeList(startTime,endTime);
        List<String>  timeList = DateUtils.getRecentMouth (endTime,13);//20191010 更改
        if(timeList == null || timeList.size()==0)
            return PlatoResult.failResult("时间段不正确");
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<Data> dataList= dataService.selectYQ90TByBankAndTimes (timeList,bank.getId());
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String[] DateValue = timeList.toArray(new String[timeList.size()]);
        String[] dataBuLiangDaiKuanValue=new String[timeList.size()];
        String[] zhanBiValue=new String[timeList.size()];
        String[] dataYuQi90TValue=new String[timeList.size()];

        for(int i=0;i<timeList.size();i++){
            String dataBuLiangDaiKuan=null,zhanBi=null,dataYuQi90T=null ;
            for(Data data:dataList){
                if(data.getDataTime().equals(timeList.get(i))){
                    switch (data.getDataName()){
                        case "不良贷款额":
                                dataBuLiangDaiKuan =  MathUtil.isNanInfinity(data.getDataValue());
                            break;
                        case "逾期90天以上贷款":
                                dataYuQi90T =   MathUtil.isNanInfinity(data.getDataValue());
                            break;
                        case "逾期90天以上贷款占比":
                                zhanBi = MathUtil.isNanInfinity(data.getDataValue());
                            break;
                    }
                }
            }
            dataBuLiangDaiKuanValue[i]=dataBuLiangDaiKuan;
            dataYuQi90TValue[i]=dataYuQi90T;
            zhanBiValue[i]=zhanBi;
        }
        JSON json = new JSONObject();
        List<String> stringList = new ArrayList<>(Arrays.asList( "不良贷款额","逾期90天以上贷款","逾期90天以上贷款占比"));
        ((JSONObject) json).put("不良贷款额", dataBuLiangDaiKuanValue);
        ((JSONObject) json).put("逾期90天以上贷款", dataYuQi90TValue);
        ((JSONObject) json).put("逾期90天以上贷款占比", zhanBiValue);
        ((JSONObject) json).put("日期", DateValue);
        ((JSONObject) json).put("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),stringList,null));
        return PlatoResult.successResult (json);
    }
    /**
     * kai_w_x
     * @param
     * @param bankName
     * @return
     * 资产结构的后置条件-本期新增不良贷款占新发放贷款的比例-显示正常类（正常 + 关注）降级为不良贷款的迁徙率-显示处置不良贷款占新形成不良贷款的比例
     * @throws Exception
     */
    @RequestMapping("buLiangDaiKuanHZTJ")
    public PlatoBasicResult getBuLiangDaiKuanHZTJ(String time,String bankName,String typeName) throws Exception {
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        Map<String,String> map=DateUtils.getRecentQuarter(time,5);
        for(Map.Entry<String, String> entry : map.entrySet()){
            sessionList.add(entry.getKey());
            dateList.add(entry.getValue()) ;
        }
        List<String> mObj = null;
        if("daiKuanQianXi".equals (typeName) ){
            mObj =  new ArrayList<>(Arrays.asList("贷款迁徙率","期初正常贷款在期末的存量","正常类迁徙为不良的金额"));
        }else if("xingZengBLiang".equals (typeName)){
            mObj =  new ArrayList<>(Arrays.asList("本期新增不良占比", "新发放贷款总额", "新增不良贷款总额"));
        }else if("chuZhiBuLiang".equals (typeName)){
            mObj =  new ArrayList<>(Arrays.asList("处置不良占新形成不良的比例","本期处置不良","本期新形成不良及其处置部分"));
        } else{
            return PlatoResult.successResult ("数据异常！请核实！！！");
        }
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.selectBuLiangDaiKuanHZTJByBankAndTimes (dateList,bank.getId(),mObj);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");

        String[] DateValue = arrayReverse (sessionList.toArray(new String[sessionList.size()]));//20191010 arrayReverse  更改  倒叙为201806 201809 201812 201903
        String[] dataZongEValue=new String[dateList.size()];
        String[] zhanBiValue=new String[dateList.size()];
        String[] dataBuLiangValue=new String[dateList.size()];
        for(int i=0;i<dateList.size();i++){
            String dataZongE=null,zhanBi=null,dataBuLiang=null ;
            for(Data data:dataList){
                if(data.getDataTime().equals(dateList.get(i))){
                    if(mObj.get(0).equals (data.getDataName())){
                            zhanBi=   MathUtil.isNanInfinity(data.getDataValue());
                    }else  if(mObj.get(1).equals (data.getDataName())){
                            dataZongE = MathUtil.isNanInfinity(data.getDataValue());
                    }else if(mObj.get(2).equals (data.getDataName())){
                            dataBuLiang =  MathUtil.isNanInfinity(data.getDataValue());
                    }else{
                        return PlatoResult.successResult ("数据异常！2请核实！！！");
                    }
                }
            }
            dataZongEValue[i]=dataZongE;
            dataBuLiangValue[i]=dataBuLiang;
            zhanBiValue[i]=zhanBi;
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put(mObj.get(0), zhanBiValue);
        ((JSONObject) json).put(mObj.get(1), dataZongEValue);
        ((JSONObject) json).put(mObj.get(2), dataBuLiangValue);
        ((JSONObject) json).put("日期", DateValue);
        ((JSONObject) json).put("dataRules", toViewRulesByDroupIdDatanames (bank.getGroupId (),mObj,null));
        return PlatoResult.successResult (json);
    }

    /**
     * 表外业务,农商行、村镇银行、国有
     * @param time
     * @param bankName
     * @return
     * @throws Exception
     */
    @RequestMapping("biaoWaiYeWu")
    public PlatoBasicResult getBiaoWaiYeWu(String time,String bankName) throws Exception{
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        if(bank.getGroupId() != 14 && bank.getGroupId() != 4 && bank.getGroupId() != 13  && bank.getGroupId() != 2 && bank.getGroupId() != 7&& bank.getGroupId() != 9){
            return PlatoResult.failResult("该机构不存在此项数据");
        }
        List<String> sessionList = new ArrayList<>();
        List<String> dateList=DateUtils.getRecentMouth(time,13);
        String[] dataValues=new String[dateList.size()];
        String stringOnlyCode=null;
        if(bank.getGroupId() == 4 ){
            stringOnlyCode="K074";//村镇银行 K074
        }else if(bank.getGroupId() == 13 ){
            stringOnlyCode="F119";//农商行
        }else if(bank.getGroupId() == 7 ){
            stringOnlyCode="H126";// 城市商业银行
        }else if(bank.getGroupId() == 2 ){
            stringOnlyCode="L129";// 国有银行
        }else if(bank.getGroupId() == 9 ){
            stringOnlyCode="M129";// 国有汇总
        }else if(bank.getGroupId() == 14 ){
            stringOnlyCode="G0142";// 联社
        }
        else if(bank.getGroupId() == 14 ){
            stringOnlyCode="G0142";// 联社银行
        }
        List<Data> dataList= dataService.selectBWYWDataByBankAndTimeCode (dateList,bank.getId(),stringOnlyCode);
        if(dataList ==null || dateList.size()==0)
            return PlatoResult.failResult("无数据");
        if(bank.getGroupId() == 7 || bank.getGroupId() == 14 || bank.getGroupId() == 13 || bank.getGroupId() ==4 || bank.getGroupId() ==2 || bank.getGroupId() ==9){      //农村商业银行 村镇
            for(int i=0;i<dateList.size();i++){
                String dataValue=null;
                for(Data data:dataList){
                    if(data.getDataTime().equals(dateList.get(i))&&data.getOnlyCode().equals(stringOnlyCode)){
                            dataValue =  MathUtil.isNanInfinity(data.getDataValue());
                        break;
                    }
                }
                dataValues[i] = dataValue;
            }
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put("date", dateList);
        ((JSONObject) json).put("value", dataValues);
        ((JSONObject) json).put ("dataRules",toViewRulesByOnlyCodes ("'"+stringOnlyCode+"'"));
        return PlatoResult.successResult(json);
    }

    /**
     * 不良清收
     * @param time
     * @param bankName
     * @return
     * @throws Exception
     */
    @RequestMapping("buLiangQingShou")
    public PlatoBasicResult getBuLiangQingShou(String time,String bankName) throws Exception{
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
//        if(bank.getGroupId() != 4 && bank.getGroupId() != 13 ){
//            return PlatoResult.failResult("该机构不存在此项数据");
//        }
        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        Map<String,String> map=DateUtils.getRecentQuarter(time,5);
        for(Map.Entry<String, String> entry : map.entrySet()){
            sessionList.add(entry.getKey());
            dateList.add(entry.getValue()) ;
        }
        String[] dateValues = sessionList.toArray(new String[sessionList.size()]);
        String[] dataValues=new String[dateList.size()];
        String stringOnlyCode=null;
        if(bank.getGroupId() == 11){
            stringOnlyCode="K078";//村镇银行汇总
        }else if(bank.getGroupId() == 4){
            stringOnlyCode="K079";//村镇银行
        }else if(bank.getGroupId() == 13 ){
            stringOnlyCode="F124";//农商行
        }else if(bank.getGroupId() == 14 ){
            stringOnlyCode="G117";//农村信用社
        }else if(bank.getGroupId() == 12 ){
            stringOnlyCode="E105";//城商行汇总
        }else if(bank.getGroupId() == 9 ){
            stringOnlyCode="M113";//大型行汇总
        }else if(bank.getGroupId() == 7 ){
            stringOnlyCode="H105";//城商行
        }  else if(bank.getGroupId() == 2 ){
            stringOnlyCode="L113";//国有银行
        }else if(bank.getGroupId() == 6 || bank.getGroupId() == 15 ) {
            stringOnlyCode = "I046";
        }
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.selectBWYWDataByBankAndTimeCode (dateList,bank.getId(),stringOnlyCode);
        if(dataList ==null || dateList.size()==0)
            return PlatoResult.failResult("无数据");
        if(bank.getGroupId() == 13 || bank.getGroupId() ==4|| bank.getGroupId() ==12|| bank.getGroupId() ==14|| bank.getGroupId() ==7|| bank.getGroupId() ==2
                || bank.getGroupId() ==9|| bank.getGroupId() ==15|| bank.getGroupId() ==6){      //农村商业银行 村镇
            for(int i=0;i<dateList.size();i++){
                String dataValue=null;
                for(Data data:dataList){
                    if(data.getDataTime().equals(dateList.get(i))&&data.getOnlyCode().equals(stringOnlyCode)){
                            dataValue =  MathUtil.isNanInfinity(data.getDataValue());
                        break;
                    }
                }
                dataValues[i] = dataValue;
            }
        }
        JSON json = new JSONObject();
        //20191010  更改arrayReverse(dateValues) 倒叙为201806 201809 201812 201903
        ((JSONObject) json).put("date", arrayReverse(dateValues));
        ((JSONObject) json).put("value", dataValues);
        ((JSONObject) json).put("dataRules",toViewRulesByOnlyCodes ("'"+stringOnlyCode+"'"));
        return PlatoResult.successResult(json);
    }


    /**
     * 表外业务的后置条件
     * @param time
     * @param bankName
     * @return
     */
    @RequestMapping("/bwyw/hztj")
    public PlatoBasicResult getBWYWHZTJ(String time,String bankName){
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<String> dateList=DateUtils.getRecentMouth(time,13);
        List<Data> dataList = null;
        if(bank.getGroupId() ==13){ // 农村商业银行
            dataList = dataService.getChildrenByParentAndBankTimes(dateList,bank.getId(),"F119");
        }else if(bank.getGroupId() == 4) { // 村镇银行
            dataList = dataService.getChildrenByParentAndBankTimes (dateList, bank.getId (), "K074");
        }else if(bank.getGroupId() == 7) {
            dataList = dataService.getChildrenByParentAndBankTimes (dateList, bank.getId (), "H126");
        }else if(bank.getGroupId() == 2){
            dataList = dataService.getChildrenByParentAndBankTimes(dateList,bank.getId(),"L129");
        }else if(bank.getGroupId() == 9){
            dataList = dataService.getChildrenByParentAndBankTimes(dateList,bank.getId(),"M129");
        }else if(bank.getGroupId() == 14){
            dataList = dataService.getChildrenByParentAndBankTimes(dateList,bank.getId(),"G0142");
        }
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        List<String> mObj = null;
        String  parentCode = null;
        if(bank.getGroupId() != 7 ){
            mObj = new ArrayList<> (Arrays.asList ("承兑汇票", "未使用的信用卡额度", "委托贷款", "代理代销业务"));
           if(bank.getGroupId ()==2){
               parentCode="L129";
           }else if(bank.getGroupId ()==9){
               parentCode="M129";
           }else if(bank.getGroupId ()==4){
               parentCode="K074";
           }else if(bank.getGroupId ()==10){
               parentCode="C119";
           }else if(bank.getGroupId ()==11){
               parentCode="D073";
           }else if(bank.getGroupId ()==13){
               parentCode="F119";
           }else if(bank.getGroupId ()==14){
               parentCode="G0142";
           }
        }else if(bank.getGroupId() == 7) {
            mObj = new ArrayList<> (Arrays.asList ("承兑汇票", "可随时无条件撤销的贷款承诺", "委托贷款合计", "代理代销业务"));
            parentCode = "H126";
        }
        String[] dataZongEValue=new String[dateList.size()];
        String[] zhanBiValue=new String[dateList.size()];
        String[] dataBuLiangValue=new String[dateList.size()];
        String[] dataDaiLiValue=new String[dateList.size()];
        for(int i=0;i<dateList.size();i++){
            String dataZongE=null,zhanBi=null,dataBuLiang=null,dataDaiLi = null;
            for(Data data:dataList){
                if(data.getDataTime().equals(dateList.get(i))){
                    if(mObj.get(0).equals (data.getDataName())){
                            zhanBi =  MathUtil.isNanInfinity(data.getDataValue());
                    }else  if(mObj.get(1).equals (data.getDataName())){
                            dataZongE = MathUtil.isNanInfinity(data.getDataValue());
                    }else if(mObj.get(2).equals (data.getDataName())){
                            dataBuLiang =  MathUtil.isNanInfinity(data.getDataValue());
                    }else if(mObj.get(3).equals (data.getDataName())){
                            dataDaiLi =  MathUtil.isNanInfinity(data.getDataValue());
                    }else{
                        return PlatoResult.successResult ("数据异常！2请核实！！！");
                    }
                }
            }
            dataZongEValue[i]=dataZongE;
            dataBuLiangValue[i]=dataBuLiang;
            zhanBiValue[i]=zhanBi;
            dataDaiLiValue[i]=dataDaiLi;
        }
        JSON json = new JSONObject();
        ((JSONObject) json).put(mObj.get(0), zhanBiValue);
        ((JSONObject) json).put(mObj.get(1), dataZongEValue);
        ((JSONObject) json).put(mObj.get(2), dataBuLiangValue);
        ((JSONObject) json).put(mObj.get(3), dataDaiLiValue);
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames(bank.getGroupId (),mObj,parentCode));
        ((JSONObject) json).put ("dataLegend",mObj);
        ((JSONObject) json).put("日期", dateList);
        return PlatoResult.successResult (json);
    }

    /**
     * 不良清收的后置条件
     * @param time
     * @param bankName
     * @return
     */
    @RequestMapping("/blqs/hztj")
    public PlatoBasicResult getBLQSHZTJ(String time,String bankName) {
        if (StringUtil.isEmpty (time)) {
            return PlatoResult.failResult ("日期不正确");
        }
        int month = Integer.valueOf(time.substring (6,7))*3;
        String newTime = time.substring (0,4)+"0"+month;
        Bank bank = bankService.getByName (bankName);
        if (bank == null) {
            return PlatoResult.failResult ("不存在该机构");
        }
        List<Data> dataList = null;
        String parentCode = null;
        if (bank.getGroupId () == 13) { // 农村商业银行
            parentCode = "F124";
        }  else if (bank.getGroupId () == 11) { // 村镇银行
            parentCode = "K078";
        }else if (bank.getGroupId () == 4) { // 村镇银行
            parentCode = "K079";
        }else if (bank.getGroupId () == 14) {
            parentCode = "G117";
        }else if (bank.getGroupId () == 12) {
            parentCode = "E105";
        }else if (bank.getGroupId () == 7) {
            parentCode = "H105";
        }else if (bank.getGroupId () == 2) {
            parentCode = "L113";
        }else if (bank.getGroupId () == 9) {
            parentCode = "M113";
        }
        dataList = dataService.getChildrenByParentAndBankTime (newTime, bank.getId (),parentCode);
        if (dataList == null || dataList.size () == 0)
            return PlatoResult.failResult ("无数据");
        List<String> mObj = new ArrayList<> (Arrays.asList ("当期", "同比", "环比","比年初"));
        List<String> mObj0 = new ArrayList<> (Arrays.asList ("不良贷款处置","以物抵债","贷款核销","其他"));
        List<String> mObj1 = new ArrayList<> (Arrays.asList ("不良贷款处置同期数","以物抵债同期数","贷款核销同期数","其他同期数"));
        List<String> mObj2 = new ArrayList<> (Arrays.asList ("不良贷款处置上期数","以物抵债上期数","贷款核销上期数","其他上期数"));
        List<String> mObj3 = new ArrayList<> (Arrays.asList ("不良贷款处置年初数","以物抵债年初数","贷款核销年初数","其他年初数"));
        String[] dataDangQiValue = new String[mObj.size ()];
        String[] dataTongBiValue = new String[mObj.size ()];
        String[] dataHuanBiValue = new String[mObj.size ()];
        String[] dataBiNianCuValue = new String[mObj.size ()];
        for (Data data : dataList){
            for(int j = 0;j<mObj.size ();j++) {
                if (mObj0.get (j).equals (data.getDataName ())) {
                        dataDangQiValue[j] = MathUtil.isNanInfinity(data.getDataValue());
                } else if (mObj1.get (j).equals (data.getDataName ())){
                        dataTongBiValue[j] =  MathUtil.isNanInfinity(data.getDataValue());
                }else if  (mObj2.get (j).equals (data.getDataName ())) {
                        dataHuanBiValue[j] = MathUtil.isNanInfinity(data.getDataValue());
                }else if(mObj3.get (j).equals (data.getDataName ())){
                        dataBiNianCuValue[j] = MathUtil.isNanInfinity(data.getDataValue());
                }
            }
        }

        JSON json = new JSONObject ();
        ((JSONObject) json).put (mObj.get (0), dataDangQiValue);
        ((JSONObject) json).put (mObj.get (1), dataTongBiValue);
        ((JSONObject) json).put (mObj.get (2), dataHuanBiValue);
        ((JSONObject) json).put (mObj.get (3), dataBiNianCuValue);
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames(bank.getGroupId (),mObj0,parentCode));
        ((JSONObject) json).put ("xData", mObj0);
        return PlatoResult.successResult (json);
    }

    /**
     * kai_w_x
     * @param
     * @param bankName
     * @return
     * 农合机构 利润比  收入比
     * @throws Exception
     */
    @RequestMapping("liRunShouRu")
    public PlatoBasicResult getLiRunShouRu(String time,String bankName,String typeName) throws Exception {
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        if(bank.getGroupId() != 4 && bank.getGroupId() != 13 && bank.getGroupId() != 14 ){
            return PlatoResult.failResult("该机构不存在此项数据");
        }
        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        Map<String,String> map=DateUtils.getRecentQuarter(time,5);
        for(Map.Entry<String, String> entry : map.entrySet()){
            sessionList.add(entry.getKey());
            dateList.add(entry.getValue()) ;
        }
        List<String> mObj = null;
        if("liRunLv".equals (typeName) ){
            mObj =  new ArrayList<>(Arrays.asList("资产利润率","资本利润率","净利润","未分配利润"));
        }else if("shouRuBi".equals (typeName)){
            mObj =  new ArrayList<>(Arrays.asList("成本收入比","中间业务收入比"));
        }  else{
            return PlatoResult.successResult ("数据异常！请核实！！！");
        }
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.selectBuLiangDaiKuanHZTJByBankAndTimes (dateList,bank.getId(),mObj);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String[] DateValue =arrayReverse (  sessionList.toArray(new String[sessionList.size()])); //20191010 arrayReverse() 更改  倒叙为201806 201809 201812 201903
        String[] dataZongEValue=new String[dateList.size()];
        String[] zhanBiValue=new String[dateList.size()];
        String[] liRunValue=new String[dateList.size()];
        String[] weiFenPeiLiRunValue=new String[dateList.size()];
        for(int i=0;i<dateList.size();i++){
            String dataZongE=null,zhanBi=null,liRun = null,weiFenPeiLiRun = null ;
            for(Data data:dataList){
                if(data.getDataTime().equals(dateList.get(i))){
                    if(mObj.get(0).equals (data.getDataName())){
                            zhanBi =   MathUtil.isNanInfinity(data.getDataValue());
                    }else  if(mObj.get(1).equals (data.getDataName())){
                            dataZongE =  MathUtil.isNanInfinity(data.getDataValue());
                    }else if(3 < mObj.size()){
                        if( mObj.get(2).equals (data.getDataName())){
                            liRun  =  MathUtil.isNanInfinity(data.getDataValue());
                        }else if(mObj.get(3).equals (data.getDataName())){
                            weiFenPeiLiRun  =  MathUtil.isNanInfinity(data.getDataValue());
                        }
                    }  else {
                        return PlatoResult.successResult ("数据异常！2请核实！！！");
                    }
                }
            }
            dataZongEValue[i]=dataZongE;
            zhanBiValue[i]=zhanBi;
            // 20191031  增加净利润   未分配利润
            if(null != liRun){
                liRunValue[i] = liRun;
                weiFenPeiLiRunValue[i] = weiFenPeiLiRun;
            }
        }

        JSON json = new JSONObject();
        ((JSONObject) json).put(mObj.get(0), zhanBiValue);
        ((JSONObject) json).put(mObj.get(1), dataZongEValue);
        if( 3 <  mObj.size() ){
            ((JSONObject) json).put( mObj.get(2), liRunValue);
            ((JSONObject) json).put( mObj.get(3), weiFenPeiLiRunValue);
        }
        ((JSONObject) json).put("日期", DateValue);
        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames(bank.getGroupId (),mObj,null));
         return PlatoResult.successResult (json);
    }
    /**
     * kai_w_x
     * @param
     * @param bankName
     * @return
     * 通用函数 村镇银行-支农支小  委托贷款情况  棚区改造情况
     * @throws Exception
     */
    @RequestMapping("generalFunctions")
    public PlatoBasicResult getGeneralFunctions(String time,String bankName,String typeName) throws Exception {
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        String  parentCode = null;
        List<String> mObj = null;
        if("znzx".equals (typeName) ){
            if(bank.getGroupId() != 4   && bank.getGroupId() != 11 ){
                return PlatoResult.failResult("该机构"+bank.getName ()+"不存在此项数据");
            }
            dateList=DateUtils.getRecentMouth(time,12);
            mObj =  new ArrayList<>(Arrays.asList("户均贷款余额","单户500万元以下贷款余额占比","农户和小微企业贷款占比"));
        }else if("xwqy".equals (typeName)){//全辖小微企业贷款
            if(bank.getGroupId() != 8 ){
                return PlatoResult.failResult("该机构"+bank.getName ()+"不存在此项数据");
            }
            dateList=DateUtils.getRecentMouth(time,13);
            mObj =  new ArrayList<>(Arrays.asList("小微企业贷款","小微企业不良贷款","小微不良率"));
        } else if("phjr".equals (typeName) ){//全辖普惠金融
            if(bank.getGroupId() != 8 ){
                return PlatoResult.failResult("该机构"+bank.getName ()+"不存在此项数据");
            }
            dateList=DateUtils.getRecentMouth(time,13);
            mObj =  new ArrayList<>(Arrays.asList("法人普惠小微贷款","法人普惠小微贷款不良贷款","法人普惠小微贷款不良率"));
        }  else if( "ldxbl".equals (typeName)|| "ldxqkl".equals (typeName) || "hxfzbl".equals (typeName) || "yzzcldxczl".equals (typeName)){//全辖普惠金融
            if(bank.getGroupId() != 13 && bank.getGroupId() != 4 && bank.getGroupId() != 11 && bank.getGroupId() != 14 && bank.getGroupId() != 10  && bank.getGroupId() != 12 && bank.getGroupId() != 8){
                return PlatoResult.failResult("该机构"+bank.getName ()+"不存在此项数据");
            }
            dateList=DateUtils.getRecentMouth(time,13);
            if("ldxbl".equals (typeName)) {
                mObj = new ArrayList<> (Arrays.asList ("流动性资产", "流动性负债", "流动性比例"));
            }else if("ldxqkl".equals (typeName)){
                mObj = new ArrayList<> (Arrays.asList ("表内外流动性缺口","表内外到期资产","流动性缺口率"));
            }else if("hxfzbl".equals (typeName)){
                mObj = new ArrayList<> (Arrays.asList ("核心负债","总负债","核心负债比例"));
            }else if("yzzcldxczl".equals (typeName)){
                mObj = new ArrayList<> (Arrays.asList ("优质流动性资产","净现金流出","优质流动性资产充足率"));
            }
        }else if("dqqx".equals (typeName)){
            if(bank.getGroupId() != 7 ){
                return PlatoResult.failResult("该机构"+bank.getName ()  +"不存在此项数据");
            }
            dateList = DateUtils.getRecentMouth(time,1);
            mObj = new ArrayList<>(Arrays.asList("地方政府债务一年内到期","地方政府债务一年到五年到期","地方政府债务五年到十年到期","地方政府债务十年以上到期"));
            parentCode="H094";
        }else if("zczl".equals (typeName)){
            if(bank.getGroupId() != 7 ){
                return PlatoResult.failResult("该机构"+bank.getName ()  +"不存在此项数据");
            }
            dateList = DateUtils.getRecentMouth(time,5);
            mObj = new ArrayList<>(Arrays.asList("地方政府债务资产质量正常类","地方政府债务资产质量关注类","地方政府债务资产质量次级类","地方政府债务资产质量可疑类","地方政府债务资产质量损失类"));
            parentCode="H094";
        }
        else if("weiTuoDaiKuan".equals (typeName) || "dfzfzw".equals(typeName)){
            if(bank.getGroupId() != 7 && bank.getGroupId() !=12 && bank.getGroupId() !=6 && bank.getGroupId() != 8 ){
                return PlatoResult.failResult("该机构"+bank.getName ()  +"不存在此项数据");
            }
            dateList=DateUtils.getRecentMouth(time,6);
            // 年初的时间  例：201812
            String newTime = (Integer.parseInt(time.substring (0,4))-1)+"12";
            dateList.add (0,newTime);
            if("weiTuoDaiKuan".equals(typeName)) {
                mObj = new ArrayList<>(Arrays.asList("金融机构委托贷款", "非金融机构委托贷款", "委托贷款比上期增速","委托贷款比年初增速"));
            }else if( "dfzfzw".equals(typeName)){
                mObj = new ArrayList<>(Arrays.asList("地方政府承诺偿还","地方政府提供担保","地方政府长期支出责任融资","地方政府增速"));
            }
        }else if("sndk".equals(typeName) || "fdc".equals(typeName) || "snlgbdy".equals(typeName) || "phq".equals(typeName)||"qxfdc".equals (typeName)||"qxfdcHZ".equals (typeName)||"zbczl".equals (typeName) ||"ggl".equals (typeName)){
            if(bank.getGroupId() != 13 && bank.getGroupId() != 7 && bank.getGroupId() != 8 && bank.getGroupId() != 10
                    && bank.getGroupId() != 12 && bank.getGroupId() != 11 && bank.getGroupId() != 6 && bank.getGroupId() != 14 && bank.getGroupId() != 4){
                return PlatoResult.failResult("该机构"+bank.getName ()  +"不存在此项数据");
            }
            if("qxfdcHZ".equals (typeName)){
                time =DateUtils.getMonthByQuarter(time);
            }
            Map<String,String> map=DateUtils.getRecentQuarter("201910",5);
            for(Map.Entry<String, String> entry : map.entrySet()){
                sessionList.add(entry.getKey());
                dateList.add(entry.getValue()) ;
            }
            Collections.reverse(dateList);
            /**
             * 20191031   更改为最近5个季度
             */
//            // 上年末的时间21xx12 例：201812
//            String newTime = (Integer.parseInt(time.substring (0,4))-1)+"12";
//            dateList.add (newTime);
//            Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
//            String newTimeJiDu  = (Integer.parseInt(time.substring (0,4))-1)+"年第4季度(年初)";
//            sessionList.add (newTimeJiDu);

            Collections.reverse(sessionList); //20191010  更改  倒叙为201806 201809 201812 201903
            if("sndk".equals(typeName) ) {
                mObj = new ArrayList<>(Arrays.asList("涉农贷款余额", "涉农贷款增速", "各项贷款增速"));
            }else if( "fdc".equals(typeName)){
                mObj = new ArrayList<>(Arrays.asList("房地产贷款余额","房地产贷款增速" ,"各项贷款增速"));
            }else if( "qxfdcHZ".equals(typeName)){//全辖房地产后置条件
                mObj = new ArrayList<>(Arrays.asList("地产开发贷款","房产开发贷款","商业用房贷款","个人住房贷款","其他房地产贷款"));
                parentCode ="A119";
            }
            else if( "qxfdc".equals(typeName)){
                mObj = new ArrayList<>(Arrays.asList("房地产贷款余额","房地产不良贷款","房地产不良率"));
            }else if( "snlgbdy".equals(typeName)){
                mObj = new ArrayList<>(Arrays.asList("涉农贷款去年同期增量","涉农贷款当期增量","涉农贷款增速","各项贷款增速"));
            }else if("phq".equals(typeName)){
                mObj =  new ArrayList<>(Arrays.asList("棚户区改造贷款余额","棚改贷款增速"));
            }else if("zbczl".equals(typeName)){
                mObj =  new ArrayList<>(Arrays.asList("资本净额","风险加权资产","资本充足率"));
            }else if("ggl".equals(typeName)){//杠杆率
                mObj =  new ArrayList<>(Arrays.asList("资本净额（杠杆率）","表内外风险资产暴露","杠杆率"));
            }
        }
        else {
            return PlatoResult.successResult ("数据异常！请核实！！！");
        }
        List<Data> dataList= dataService.selectBuLiangDaiKuanHZTJByBankAndTimes (dateList,bank.getId(),mObj);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String[][] dataWebFront = new String[mObj.size ()][dateList.size()];
        for(int i=0;i<dateList.size();i++){
            String[] dataI= new String[mObj.size ()];
            for(Data data:dataList){
                if(data.getDataTime().equals(dateList.get(i))) {
                   for(int j=0;j<mObj.size ();j++) {
                       if (mObj.get (j).equals (data.getDataName ())) {
                               dataI[j] =  MathUtil.isNanInfinity(data.getDataValue());
                       }
                   }
                }
            }
            if(dataI != null){
                for(int j=0;j<mObj.size ();j++) {
                    dataWebFront[j][i] = dataI[j];
                }
            }else {
                return PlatoResult.successResult ("数据异常！请核实！！！");
            }
        }
        JSON json = new JSONObject();
            for(int i=0;i<mObj.size ();i++){
                ((JSONObject) json).put(mObj.get(i), dataWebFront[i]);
            }
            if("phq".equals (typeName) || "sndk".equals (typeName)||"fdc".equals(typeName)||"qxfdc".equals(typeName)||"snlgbdy".equals(typeName)||"phq".equals(typeName)||"qxfdcHZ".equals (typeName)||"zbczl".equals (typeName) ||"ggl".equals (typeName)){
                ((JSONObject) json).put("xAxisData", sessionList.toArray(new String[sessionList.size()]));
                ((JSONObject) json).put("legendData",mObj);
            }else if("dqqx".equals(typeName)) {
                ((JSONObject) json).put ("xAxisData", mObj);
                String[] dataValues=new String[mObj.size()];
                for (int i = 0;i<dataList.size();i++){
                    dataValues[i]=dataList.get(i).getDataValue();
                }
                ((JSONObject) json).put ("dataValues", dataValues);
            }else if("dfzfzw".equals(typeName) || "weiTuoDaiKuan".equals(typeName)){
                ((JSONObject) json).put("xAxisData", dateList);
                ((JSONObject) json).put("legendData",mObj);
          } else{
                ((JSONObject) json).put("日期", dateList);
            }
        ((JSONObject) json).put("legendData",mObj);

        ((JSONObject) json).put ("dataRules",toViewRulesByDroupIdDatanames (bank.getGroupId (),mObj,parentCode));

        return PlatoResult.successResult (json);
    }
       /**  * kai_w_x
     * @param
     * @param bankName
     * @return
             * 通用函数 code 两高两剩
     * @throws Exception
     */
    @RequestMapping("generalFunctionsByCode")
    public PlatoBasicResult generalFunctionsByCode(String time,String bankName,String typeName) throws Exception {
        if(StringUtil.isEmpty(time)){
            return PlatoResult.failResult("日期不正确");
        }
        Bank bank = bankService.getByName(bankName);
        if(bank == null){
            return PlatoResult.failResult("不存在该机构");
        }
        List<String> dateList = new ArrayList<>();
        List<String> sessionList = new ArrayList<>();
        String onlyCodes = null;
        if("LGLS".equals (typeName) ){//两高两剩
            if(bank.getGroupId() != 2 && bank.getGroupId() != 9 ){
                return PlatoResult.failResult("该机构"+bank.getName ()  +"不存在此项数据");
            }
            Map<String,String> map=DateUtils.getRecentQuarter(time,3);
            for(Map.Entry<String, String> entry : map.entrySet()){
                sessionList.add(entry.getKey());
                dateList.add(entry.getValue()) ;
            }
            // 上年末的时间21xx12 例：201812
            String newTime = (Integer.parseInt(time.substring (0,4))-1)+"12";
            dateList.add (newTime);
            String newTimeJiDu  = (Integer.parseInt(time.substring (0,4))-1)+"年第4季度(年初)";
            sessionList.add (newTimeJiDu);
            if(bank.getGroupId ()==2){
                onlyCodes = "L103";
            }else if(bank.getGroupId ()==9){
                onlyCodes = "M103";
            }
        } else {
            return PlatoResult.successResult ("数据异常！请核实！222！！");
        }
        Collections.reverse(dateList); //20191010  更改  倒叙为201806 201809 201812 201903
        Collections.reverse(sessionList); //20191010  更改  倒叙为201806 201809 201812 201903
        List<Data> dataList= dataService.selectBWYWDataByBankAndTimeCode (dateList,bank.getId(),onlyCodes);
        if(dataList ==null || dataList.size()==0)
            return PlatoResult.failResult("无数据");
        String[] dataValues=new String[dateList.size()];
        for(int i=0;i<dateList.size();i++){
            String dataValue=null;
            for(Data data:dataList){
                if(data.getDataTime().equals(dateList.get(i))&&data.getOnlyCode().equals(onlyCodes)){
                    dataValue = MathUtil.isNanInfinity(data.getDataValue());
                    break;
                }
            }
            dataValues[i] = dataValue;
        }
        JSON json = new JSONObject();
        if("LGLS".equals (typeName)){
            ((JSONObject) json).put("日期", sessionList.toArray(new String[sessionList.size()]));
        }else {
            ((JSONObject) json).put ("日期", dateList);
        }
           ((JSONObject) json).put("dataList",dataValues);
           ((JSONObject) json).put("dataRules",toViewRulesByOnlyCodes ("'"+onlyCodes+"'"));
        return PlatoResult.successResult (json);
    }

    @RequestMapping("/generalFunctionsByParentCode")
    public PlatoBasicResult generalFunctionsByParentCode(String time,String bankName) {
        if (StringUtil.isEmpty (time)) {
            return PlatoResult.failResult ("日期不正确");
        }
        Bank bank = bankService.getByName (bankName);
        if (bank == null) {
            return PlatoResult.failResult ("不存在该机构");
        }
        List<Data> dataList = null;
        List<String> dateList = new ArrayList<>();
        dateList=DateUtils.getRecentMouth(time,13);
        String parentCode = null;
        if (bank.getGroupId () == 8) {
            parentCode = "A115";
        } else if (bank.getGroupId () == 10) {
            parentCode = "C003";
        }
        dataList = dataService.getChildrenByParentAndBankTimes (dateList,bank.getId (),parentCode);
        if (dataList == null || dataList.size () == 0)
            return PlatoResult.failResult ("无数据");
        for (Data data : dataList) {
            data.setDataValue (MathUtil.isNanInfinity (data.getDataValue ()));
        }
        //20191015 页面返回dataList   更改为JSON  增加取数规则
        JSON json = new JSONObject ();
        List<String> stringList = new ArrayList<> (Arrays.asList ("小微企业贷款户数"));
        ((JSONObject) json).put ("dataRules", toViewRulesByDroupIdDatanames (bank.getGroupId (), stringList, parentCode));
        ((JSONObject) json).put ("dataList", dataList);
        ((JSONObject) json).put ("axisX", dateList);
        return PlatoResult.successResult (json);
    }

//    产生一个新数组按逆序存放原数组的元素
    public String[] arrayReverse(String [] reverseArray) {
        String[] reverseArray1 =new String[reverseArray.length];
        for (int i = 0; i < reverseArray.length; i++) {
            reverseArray1[i] = reverseArray[reverseArray.length - i-1];
        }
        return reverseArray1;
    }

    // 向页面返回取数规则
    public  List<DataInfo>  toViewRulesByOnlyCodes(String onlyCodes){
        List<DataInfo>  dataInfoList = dataInfoService.toViewRulesByOnlyCodes (onlyCodes.trim ());
        return dataInfoList ;
    }

    // 向页面返回取数规则
    public  List<DataInfo>  toViewRulesByDroupIdDatanames(Integer groupId, List<String> ombj,String parent){
            String dataNames = " ";
            for(String str:ombj){
                dataNames = dataNames+ "'"+ str+"',";
            }
        List<DataInfo>  dataInfoList = dataInfoService.toViewRulesByGroupIdDataNames (groupId,dataNames.substring(0,dataNames.length()-1),parent);
        return dataInfoList ;
    }

    /**
     *
     * @param time
     * @param bankName
     * @return
     */
    @RequestMapping("/tableByName")
    public PlatoBasicResult getTableByName( String time,String bankName,String typeName){
        Bank bank = bankService.getByName(bankName);
        if(null == bank){
            return PlatoResult.failResult("该机构数据异常：请刷新页面");
        }
        if("jzd".equals (typeName)) {//集中度
            String date = DateUtils.getLastQuarter (time);
            List<DataJZD> vallues = dataJZDService.getByBankIdAndTime (bank.getId (), date);
            return PlatoResult.successResult (vallues);
        }else if("QnyjYuqi90East".equals (typeName)||"quanXiaTiHuiDai".equals (typeName)){
            List<String>  mObj = null;
            String dataName ="";
            if("QnyjYuqi90East".equals (typeName)){
                dataName="逾期90天以上不良贷款";
                mObj =new ArrayList<>(Arrays.asList("都匀农商行","荔波农商行","三都农商行","福泉农商行","瓮安农商行","贵定联社","龙里农商行","长顺联社","罗甸联社","平塘农商行","惠水农商行","独山农商行"));
            }else if("quanXiaTiHuiDai".equals (typeName)){
                dataName="特惠贷";
                mObj =new ArrayList<>(Arrays.asList("都匀农商银行","荔波农商银行","三都农商银行","福泉农商银行","瓮安农商银行","贵定联社","龙里农商银行","长顺联社","罗甸联社","平塘农商银行","惠水农商银行","独山农商银行"));
            }else{
                return PlatoResult.failResult ("数据异常");
            }
            List<QnyjYuqi90East> dataList= qnyjYuqi90EastService.selectByTime (time,dataName);
            if(0 == dataList.size ()){
                return  PlatoResult.failResult ("当前无数据");
            }
            String[] dataI= new String[mObj.size ()];
            for(int i=0;i<mObj.size();i++){
                for(QnyjYuqi90East data:dataList){
                        for(int j=0;j<mObj.size ();j++) {
                            if (mObj.get (j).equals (data.getBankname ())) {
                                dataI[j] =  MathUtil.isNanInfinity(data.getDatavalue ().toString ());
                            }
                        }
                    }
                }
            JSON json = new JSONObject();
            ((JSONObject) json).put("xDate", mObj);
            ((JSONObject) json).put("dataValue", dataI);
            return PlatoResult.successResult (json);
        }
        return  PlatoResult.failResult ("数据异常！请联系管理员");
    }
}
