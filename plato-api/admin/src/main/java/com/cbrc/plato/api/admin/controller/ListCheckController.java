package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cbrc.plato.core.basic.model.*;
import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.excel.ExcelUtils;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.Thread.sleep;


@Slf4j
@RestController
@RequestMapping("/checkTables")
public class ListCheckController {

    @Autowired
//    IReportMouldService reportMouldService;
     IListCheckMouldService listCheckMouldService;
    @Autowired
    IReportCellService reportCellService;

    @Autowired
    IListCheckService listCheckService;

    @Autowired
    IBankService bankService;

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IqnyjDicService qnyjDicService;

    @Autowired
    IBankGroupService bankGroupService;

    @Autowired
    IExcelMouldService excelMouldService;

    @RequestMapping("/module")
    //    @RequiresPermissions("mould:list")
    public PlatoBasicResult modulePage(int mid) {
        ReportMould reportMould = listCheckMouldService.getById (mid);
        if (reportMould == null)
            return PlatoResult.failResult ("模板不存在！");
        /*读取模板表，获取取数规则！*/
        Map<String, String> rules = null;
        try {
            log.info (reportMould.getPath ());
            rules = ExcelRead.readReportMouldCells1 (reportMould.getPath (), 0);
        } catch (Exception e) {
            e.printStackTrace ();
            return PlatoResult.failResult ("模板表：" + reportMould.getName () + "错误，无法读取取数规则！");
        }
        if (rules == null)
            return PlatoResult.failResult ("模板表：" + reportMould.getName () + "错误，无法读取取数规则！");

        //  解析rues  得到checkTables 对象集
        List<CheckTable> checkTables = reportCellService.parseReportCells1(rules);
        List<ReportCell> reportCells = new ArrayList<> ();
//        for(int i = 0;i < checkTables.size();i++){
//            log.info ("checkTables:"+checkTables.get (i).getToCheckTarget ()+":"+checkTables.get (i).getCheckRule ()+":"+checkTables.get (i).getCheckTarget ()+":"+checkTables.get (i).getMonth ());
//        }
        listCheckService.checkTableForSource (checkTables);// 表格来源
        return PlatoResult.successResult (checkTables);
    }

    /*贵阳银行 贵州银行 报表关系效验 checkTables bbgxxy*/
    @RequestMapping("bbgxxy")
    public   PlatoBasicResult czyhzyzbjcb( int mid,String time,Integer bankId ,int dataSource) {
        long startTime = System.currentTimeMillis ();

        QnyjDic qnyjDic = qnyjDicService.selectByPrimaryKey (mid);
        if (null == qnyjDic)
            return PlatoResult.failResult ("模板不存在！");
        List<CheckTable> checkTableArrayList = listCheckService.selectByMouldId (mid);
        //  解析rues  得到checkTables 对象集
        List<ReportCell> reportCells1 = new ArrayList<> ();
        for (int i = 0; i < checkTableArrayList.size (); i++) {
            ReportCell reportCell = new ReportCell ();
            reportCell.setDataRule (checkTableArrayList.get (i).getToCheckTarget ());// 获取待校验目标目标
            reportCell.setMonth (checkTableArrayList.get (i).getMonth ());
            reportCell.setCell (checkTableArrayList.get (i).getTableForSourceCell ());
            reportCells1.add (reportCell);
        }
        List<ReportCell> reportCells2 = new ArrayList<> ();
        for (int i = 0; i < checkTableArrayList.size (); i++) {
//            log.info ("checkTables:"+checkTables.get (i).getToCheckTarget ()+":"+checkTables.get (i).getCheckRule ()+":"+checkTables.get (i).getCheckTarget ()+":"+checkTables.get (i).getMonth ());
            ReportCell reportCell = new ReportCell ();
            reportCell.setDataRule (checkTableArrayList.get (i).getCheckTarget ());// 获取校验目标
            reportCell.setMonth (checkTableArrayList.get (i).getMonth ());
            reportCell.setCell (checkTableArrayList.get (i).getTableForSourceCell ());
            reportCells2.add (reportCell);
        }
        // 非季度时间校验表
        if (!(time.endsWith ("03") || time.endsWith ("06") || time.endsWith ("09") || time.endsWith ("12"))) {
            Iterator<CheckTable> it_b = checkTableArrayList.iterator ();
            while (it_b.hasNext ()) {
                CheckTable checkTable = it_b.next ();
                if (checkTable.getMonth () == 0) {
                    it_b.remove ();
                }
            }
        }
        // 获取待校验目标目标
        List<CheckTable>  checkTables1 = null;
        // 获取校验目标
        List<CheckTable>  checkTables2 = null;
        try {
             checkTables1 = listCheckService.parseReportCells1 (checkTableArrayList, reportCells1, time, bankId);
             checkTables2 = listCheckService.parseReportCells1 (checkTableArrayList, reportCells2, time, bankId);
        }catch (Exception e) {
            PlatoResult.failResult (e.getMessage());
            if (e.getMessage().indexOf("D:\\fileManager\\file\\excle\\")!=-1) {
            return PlatoResult.failResult (e.getMessage().replace("D:\\fileManager\\file\\excle\\","  "));
            }else {
                return PlatoResult.failResult(e.getMessage());
            }
        }
        if (null == checkTables1 || null == checkTables2) {
            return PlatoResult.failResult ("---------取数异常-----------");
        }
        for(int i = 0;i < checkTableArrayList.size();i++){
            checkTableArrayList.get (i).setToCheckTargetValue (checkTables1.get (i).getCheckResults ());
            checkTableArrayList.get (i).setCheckTargetValue (checkTables2.get (i).getCheckResults ());
            checkTableArrayList.get(i).setBankName (checkTables1.get (i).getBankName ());
            if( null !=checkTables1.get (i).getCheckResults () && null !=checkTables1.get (i).getCheckResults ()) {
//                double ss = (Double.parseDouble (checkTables2.get (i).getCheckResults ()));
//                double ss2 = (Double.parseDouble (checkTables1.get (i).getCheckResults ()));
//                checkTableArrayList.get (i).setCheckResults ( String.valueOf(ss2-ss));
                if("9999999.0".equals (checkTableArrayList.get (i).getCheckTargetValue ()) || "9999999.0".equals (checkTableArrayList.get (i).getToCheckTargetValue ()) ){
                    checkTableArrayList.get (i).setIsWarning (0);// 0  校验失败！！！
                }else {
//                BigDecimal  toCheckTargetValue = new BigDecimal ( Double.parseDouble(checkTableArrayList.get (i).getToCheckTargetValue ()));
//                BigDecimal checkTargetValue =new BigDecimal ( Double.parseDouble(checkTableArrayList.get (i).getCheckTargetValue ()));
                    double toCheckTargetValue = Double.parseDouble (checkTableArrayList.get (i).getToCheckTargetValue ());
                    double checkTargetValue = Double.parseDouble (checkTableArrayList.get (i).getCheckTargetValue ());
                    // 保存4位小数
                    double toCheckTargetValueTo4Decimal = new BigDecimal (Double.parseDouble (checkTableArrayList.get (i).getToCheckTargetValue ())).setScale (3, BigDecimal.ROUND_HALF_UP).doubleValue ();
                    double checkTargetValueTo4Decimal = new BigDecimal (Double.parseDouble (checkTableArrayList.get (i).getCheckTargetValue ())).setScale (3, BigDecimal.ROUND_HALF_UP).doubleValue ();
                    checkTableArrayList.get (i).setToCheckTargetValue (String.valueOf (toCheckTargetValueTo4Decimal));
                    checkTableArrayList.get (i).setCheckTargetValue (String.valueOf (checkTargetValueTo4Decimal));
                    //setIsWarning    1 成功   0 检验失败
                    double errorRange = 0.001; //误差范围

                    if ("!=".equals (checkTableArrayList.get (i).getCheckRule ())) {
                        if (Math.abs (toCheckTargetValue - checkTargetValue) < errorRange) {
                            checkTableArrayList.get (i).setIsWarning (1);
                        } else {
                            checkTableArrayList.get (i).setIsWarning (0);
                        }
                    } else if ("=".equals (checkTableArrayList.get (i).getCheckRule ())) {
                        if (Math.abs (toCheckTargetValue - checkTargetValue) < errorRange) {
                            checkTableArrayList.get (i).setIsWarning (0);
                        } else {
                            checkTableArrayList.get (i).setIsWarning (1);
                        }
                    } else if ("<".equals (checkTableArrayList.get (i).getCheckRule ())) {
                        if (Math.abs (toCheckTargetValue - checkTargetValue) < errorRange & (toCheckTargetValue - checkTargetValue) < 0) {
                            checkTableArrayList.get (i).setIsWarning (0);
                        } else {
                            checkTableArrayList.get (i).setIsWarning (1);
                        }
                    } else if (">".equals (checkTableArrayList.get (i).getCheckRule ())) {
                        if (Math.abs (toCheckTargetValue - checkTargetValue) > errorRange & (toCheckTargetValue - checkTargetValue) > 0) {
                            checkTableArrayList.get (i).setIsWarning (0);
                        } else {
                            checkTableArrayList.get (i).setIsWarning (1);
                        }
                    } else {
                        checkTableArrayList.get (i).setIsWarning (0);
                    }
                }
           }
        }

        listCheckService.checkTableForSource (checkTableArrayList);// 表格来源
        return PlatoResult.successResult(checkTableArrayList);
    }


    // 校验规则读取 返回到页面
    @RequestMapping(value="/readCheckingRule", method = RequestMethod.POST)
    //@RequiresPermissions("excelJson:list")
    public PlatoBasicResult checkTableUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        //TODO 业务逻辑，通过excelFile.getInputStream()，处理Excel文件
        /*读取模板表，获取取数规则！*/
        Map<String, String> rules = ExcelUtils.readReportMouldCells(file);
        log.info ("---"+rules);
        //  解析rues  得到checkTables 对象集
        List<CheckTable> checkTables = reportCellService.parseReportCells1(rules);
//        for(int i = 0;i < checkTables.size();i++) {
//            log.info ("checkTables:" + checkTables.get (i).getToCheckTarget () + ":" + checkTables.get (i).getCheckRule () + ":" + checkTables.get (i).getCheckTarget () + ":" + checkTables.get (i).getMonth ());
//        }
        return PlatoResult.successResult(checkTables);
    }

    // 校验规则读取 提交到服务器server
    @RequestMapping(value="/uploadCheckingRule", method = RequestMethod.POST)
    //@RequiresPermissions("excelJson:list")
    public  @ResponseBody PlatoBasicResult checkTableUploadToServer(@RequestBody String  checkTables)  {
        Gson g = new Gson();
        JsonObject obj = g.fromJson(checkTables, JsonObject.class);
        // 银行机构groupId
        int groupId = Integer.parseInt(obj.get("moudId").toString());
        //
        String checkTablesString = obj.get("checkTables").toString();


        // 需要存储的校验规则名称
        String checkTableName = obj.get("checkTableName").toString();
        if(checkTableName.startsWith ("\"")){
            checkTableName = checkTableName.replace ("\"","");
        }if(checkTableName.endsWith ("\"")){
            checkTableName = checkTableName.replace ("\"","");
        }
        log.info ("-----checkTablesString-------"+checkTablesString);
        List<CheckTable> itmes = (List<CheckTable>)JSONArray.parseArray(checkTablesString, CheckTable.class);
        if(null != qnyjDicService.selectByQnyjDicName (checkTableName)){
            return PlatoResult.failResult("改规则名称已经存在！");
        }
        QnyjDic qnyjDic = new QnyjDic ();
        qnyjDic.setDicName (checkTableName);
        qnyjDic.setDicCode (" ");
        qnyjDic.setDicValueId (groupId);
        Timestamp time = DateUtils.getCurrentTimestamp();
        qnyjDic.setParam1 (time.toString ());
        qnyjDicService.insert (qnyjDic);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
        Integer  mouldId = qnyjDicService.selectByQnyjDicName (checkTableName).getDicId ();
        if(null != mouldId){
            List<CheckTable> insertList = new ArrayList<CheckTable>();
            for(CheckTable list: itmes){
                list.setMouldId (mouldId);
                insertList.add(list);
            }
        }else {
            return PlatoResult.successResult("添加"+qnyjDic.getDicName ()+"数据异常");
        }
         listCheckService.insertBatchList (itmes);
        return PlatoResult.successResult("success");
    }
     @RequestMapping("/checkingRulesList")
     public PlatoBasicResult getRulesNameList(){
          List<QnyjDic> qnyjDicList =   qnyjDicService.selectAll ();
          for (QnyjDic qnyjDic:qnyjDicList){
              qnyjDic.setParam1 (bankGroupService.getById (qnyjDic.getDicValueId ()).getName ());
          }
            return PlatoResult.successResult(qnyjDicList);
     }
    @RequestMapping("/checkingGroupBankNameList")
    public PlatoBasicResult getCheckingGroupBankNameList(){
        List<BankGroup> bankGroups =   bankGroupService.getBankGroupList ();
        return PlatoResult.successResult(bankGroups);
    }



    @RequestMapping(value="/bankNameList" , method = RequestMethod.GET )
    public PlatoBasicResult getCheckingBankNameList(String bankGroupId){
        log.info("rule==="+bankGroupId);
        List<Bank> banks = bankService.getBankByGroupId (Integer.parseInt(bankGroupId));
        return PlatoResult.successResult(banks);
    }


    @RequestMapping(value="/upload",method = RequestMethod.POST)
    public @ResponseBody PlatoBasicResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        //先将获取到的文件上传到临时文件夹
        String fileName = file.getOriginalFilename();//文件名
//        保存的路径
        String filePath = "D:/temp/"+currentUser.getUserName ()+"/";
        Date date = DateUtils.getCurrentTime();
        if(StringUtil.isEmpty(fileName)){
            return PlatoResult.failResult("上传文件名为空，请检查文件重新上传!");
        }
        String postfix = FileUtil.getPostfix(fileName);
        if(StringUtil.isEmpty(postfix)) {
            return PlatoResult.failResult("上传文件后缀名为空，请检查文件重新上传!");
        }
        try {
            FileUtil.uploadFile(file.getBytes(),filePath,fileName);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            return PlatoResult.failResult("文件上传失败！");
        }
        log.info("接收到上传文件:" + file.getOriginalFilename() + ",文件上传结束");
        ZipFile zip = new ZipFile(filePath+fileName, "GBK");
        List<ExcelMould> excelInfos = new ArrayList<>();
        Enumeration<?> entries = zip.getEntries();
        List<String> directory = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，获取文件夹名称
            if (entry.isDirectory()) {
                directory.add(entry.getName());
            } else {
                String excelName = null;
                if(directory.size()>0){
                    for(String fpath :directory){
                        if(entry.getName().indexOf(fpath)!=-1){
                            excelName = entry.getName().replaceAll(fpath,"");
                        }
                    }
                }
                if(excelName == null)
                    excelName=entry.getName().trim();
                String fileType = FileUtil.getPostfix(entry.getName());
                if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(fileType) || Common.OFFICE_EXCEL_2003_POSTFIX.equals(fileType)){
                    Map info = null;
                    try {
                        info = FileUtil.parseExcel(excelName);
                    } catch (Exception e) {
                        throw new IOException(excelName);
                    }
                    if (info == null) {
                        if(zip != null){
                            zip.close();
                        }
                        return null;
                    }
                    String outPath = filePath +excelName;
                    File targetFile = new File( outPath);
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    //输出文件路径信息
                    InputStream in = zip.getInputStream(entry);
                    OutputStream out = new FileOutputStream (outPath);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while((len=in.read(buf1))>0){
                        out.write(buf1,0,len);
                    }
                    log.info("excelInfo:"+info.toString());
                    in.close();
                    out.close();
                }else{
                    log.info("**不是Excel文件");
                    continue;
                }
            }
        }
        if(zip != null){
            zip.close();
        }
//        return PlatoResult.successResult("文件上传校验成功，共上传了"+total+"条数据！");
        return PlatoResult.successResult(fileName);
    }

    @RequestMapping("/checkTableRules")
    public PlatoBasicResult listpage(Integer  pageNo,Integer  pageSize , String mid){
//                log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<CheckTable>  checkTables =  this.listCheckService.selectByMouldId (Integer.parseInt (mid));
        if(checkTables.size ()==0){
            return PlatoResult.successResult ("error");
        }
        QnyjDic qnyjDic =  qnyjDicService.selectByPrimaryKey (Integer.parseInt (mid));
        BankGroup bankGroup =  bankGroupService.getById (qnyjDic.getDicValueId ());
        String institutions = bankGroup.getName ();
        int monthNumber =0;
        int seasonNumber = 0;
        for(CheckTable checkTable:checkTables){
            checkTable.setParam1 (institutions);
            if(checkTable.getMonth ()==1){//月报
                monthNumber +=1;
            }else if(checkTable.getMonth ()==0) {//季报
                seasonNumber+=1;
            }
        }
        listCheckService.checkTableForSource (checkTables);// 表格来源
        PageInfo<CheckTable> checkTablePageInfo = new PageInfo<>(checkTables);
        JSON json = new JSONObject ();
        ((JSONObject) json).put("pageInfo", checkTablePageInfo);
        ((JSONObject) json).put("monthNumber", monthNumber);
        ((JSONObject) json).put("seasonNumber", seasonNumber);
        return PlatoResult.successResult(json);
    }


    // 校验规则add
    @RequestMapping(value="/editCheckingRule", method = RequestMethod.POST)
    //@RequiresPermissions("excelJson:list")
    public  @ResponseBody PlatoBasicResult editCheckingRule(@RequestBody String  checkTable)  {
        Gson g = new Gson();
        JsonObject obj = g.fromJson(checkTable, JsonObject.class);
        String checkTablesString = obj.get("checkTable").toString();
        CheckTable checkTable1 = (CheckTable)g.fromJson(checkTablesString, CheckTable.class);

        if(null!=checkTable1){
            listCheckService.updateByPrimaryKeySelective (checkTable1);
        }else{
            return PlatoResult.failResult ("服务器异常！");
        }
        return PlatoResult.successResult("success");
    }

    // 校验规则add
    @RequestMapping(value="/addCheckingRule", method = RequestMethod.POST)
    //@RequiresPermissions("excelJson:list")
    public  @ResponseBody PlatoBasicResult addCheckingRule(@RequestBody String  checkTable)  {
        Gson g = new Gson();
        JsonObject obj = g.fromJson(checkTable, JsonObject.class);
        String checkTablesString = obj.get("checkTable").toString();
        CheckTable checkTable1 = (CheckTable)g.fromJson(checkTablesString, CheckTable.class);
        if(null!=checkTable1){
            listCheckService.insertSelective (checkTable1);
        }else{
            return PlatoResult.failResult ("服务器异常！");
        }
        return PlatoResult.successResult("success");
    }

    @RequestMapping("/delCheckingRule")
    //    @RequiresPermissions("mould:list")
    public PlatoBasicResult listpage( String id) throws Exception {
          int checkTableId = Integer.parseInt (id);
          listCheckService.deleteByPrimaryKey (checkTableId);
        return PlatoResult.successResult ("success");
    }

    @RequestMapping("/fuzzySearching")
    public  PlatoBasicResult fuzzySearching( Integer mouldId,String xuanXiang,String keyword) throws Exception {
        String projectTargetName = null;
        String projectName = null;
        String toCheckTarget =null;
        String checkTarget = null;
        if(xuanXiang .equals ( "projectTargetName")){
            projectTargetName =keyword;
        }else if(xuanXiang .equals( "projectName")){
            projectName = keyword;
        }else if(xuanXiang.equals("toCheckTarget")){
            toCheckTarget = keyword;
        }else if(xuanXiang .equals ( "checkTarget")){
            checkTarget=keyword;
        }else {
            return PlatoResult.failResult ("error");
        }
        List<CheckTable>  checkTables =  listCheckService.fuzzySearching (mouldId,projectTargetName,projectName,toCheckTarget,checkTarget);
        listCheckService.checkTableForSource (checkTables);// 表格来源
        return PlatoResult.successResult (checkTables);
    }

    @RequestMapping("/deleteMould")
    public  PlatoBasicResult deleteMould( Integer mouldId) throws Exception {
          if(mouldId != null){
              listCheckService.deleteByPrimaryKeyAll (mouldId);
             qnyjDicService.deleteByPrimaryKey (mouldId);
          }else {
              return PlatoResult.failResult ("服务器异常！");
          }
            return PlatoResult.successResult ("SUCCESS");
    }
}
