package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.model.QnyjExcelUploadError;
import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.util.datarule.model.QnyjYuqi90East;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.response.PlatoResultCodeEnum;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelFileController {

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IBankService bankService;

    @Autowired
    IBankGroupService bankGroupService;

    @Autowired
    IQnyjExcelUploadErrorService excelUploadErrorService;

    @Autowired
    IPermissionService iPermissionService;

    @Autowired
    IYuqi90EastService iYuqi90EastService;

    @RequestMapping("/upload")
    @RequiresPermissions("excel:upload")
    public @ResponseBody PlatoBasicResult upload(@RequestParam("file") MultipartFile file){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        log.info("----------------"+currentUser.getUserName()+"upload----------------");
        //先将获取到的文件上传到临时文件夹
        String fileName = file.getOriginalFilename();//文件名
        int num = 0;//上传失败文件数
        int total = 0;//上传总的文件数
        List<PlatoExcelFile> noList = new ArrayList<PlatoExcelFile>();
        List<QnyjExcelUploadError> errorList = new ArrayList<QnyjExcelUploadError>();
        Date date = DateUtils.getCurrentTime();
        if(StringUtil.isEmpty(fileName)){
            return PlatoResult.failResult("文件名为空，请检查文件重新上传!");
        }
        String postfix = FileUtil.getPostfix(fileName);
        if(StringUtil.isEmpty(postfix)) {
            return PlatoResult.failResult("文件后缀名为空，请检查文件重新上传!");
        }
        if(Common.zip_POSTFIX.equals(postfix)){
            log.info("----Zip文件");
            try {
                //解压zip文件
                String saveName = DateUtils.format(date,DateUtils.DATA_FORMAT)+"_"+currentUser.getUserRealName()+"_"+fileName;
                PlatoBasicResult saveResult = excelFileService.upLoadZipFile(file,saveName);
                if(saveResult.getCode() == PlatoResultCodeEnum.FAIL){
                    log.info("解压失败");
                    return saveResult;
                }
                List<PlatoExcelFile> platoExcelFileList = (List<PlatoExcelFile>) saveResult.getData();
                Map<String,List<PlatoExcelFile>> excelMap = ckeckExcelInfo(platoExcelFileList,currentUser);
                if(excelMap.get("updateList")==null){
                    return PlatoResult.failResult("zip文件解压无数据，请检查文件重新上传!");
                }
                noList = excelMap.get("noList");
                for (PlatoExcelFile platoExcelFile :noList) {
                    platoExcelFile.setBankName("失败");
                }
                excelFileService.insertsNew(excelMap.get("updateList"));
                total = platoExcelFileList.size();
                num = excelMap.get("noList").size();
            }catch (Exception e){
                String message = "Zip压缩包解析异常，压缩包内表【"+e.getMessage()+"】命名不规范，请检查后上传！";
                return PlatoResult.failResult(message);
            }
        }else if(Common.OFFICE_EXCEL_2010_POSTFIX.equals(postfix) || Common.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
            log.info("----Excel文件");
            //逾期90天文件
            if(fileName.contains("逾期90天非不良贷款")) {
                try {
                    try {
                        FileUtil.uploadFile(file.getBytes(), Common.EXCEL_OVERDUE_90_FILE_PATH, fileName);
                    } catch (Exception e) {
                        log.info("保存失败：" + e.toString());
                        return PlatoResult.failResult("excel文件上传保存失败，失败原因【" + e.getMessage() + "】");
                    }
                    //进行文件读取并插入数据库
                    List<QnyjYuqi90East> dataYuqi90 = ExcelRead.readYuqi90(Common.EXCEL_OVERDUE_90_FILE_PATH + fileName);
                    if (dataYuqi90.size() > 0) {
                        try {
                            iYuqi90EastService.updateByPrimaryKey(dataYuqi90);
                            return PlatoResult.successResult("文件上传解析成功！");
                        } catch (Exception e) {
                            return PlatoResult.failResult("请联系管理员，文件上传解析失败！" + "失败原因【" + e.toString() + "】");
                        }
                    }
                    //insertDatas.addAll(dataYuqi90);
                } catch (Exception e) {
                    String message = "Excel文件解析异常，表【" + e.getMessage() + "】命名不规范，请检查后上传！";
                    return PlatoResult.failResult(message);
                }

            }else {
                try {
                    //获取文件类型（月报、季报、年报）
                    Map info = null;
                    try {
                        info = FileUtil.parseExcel(fileName);
                    } catch (Exception e) {
                        throw new IOException(fileName);
                    }

                    //村镇银行有两张G04，系统只用G04利润表，故需屏蔽G04附注利润表
                    if(info.get("bankName").toString().contains("村镇银行")&&info.get("excelCode").toString().equals("G04")&&info.get("excelName").toString().contains("附注利润表"))
                        return PlatoResult.failResult("请上传：G04 利润表{16年启用}");

                    total = 1;
                    QnyjExcelUploadError qnyjExcelUploadErrors = new QnyjExcelUploadError();
                    Bank bank = bankService.getByName(info.get("bankName").toString());
                    if(null == bank){
                        log.info("表机构不存在，请检查后重新上传！");
                        qnyjExcelUploadErrors.setFailReason("机构名【"+info.get("bankName")+"】不存在，请检查文件重新上传!");
                        qnyjExcelUploadErrors.setFileName(fileName);
                        qnyjExcelUploadErrors.setCreateAuthor(currentUser.getUserName());
                        qnyjExcelUploadErrors.setCreateTime(DateUtils.format(date,DateUtils.FULL_ST_FORMAT));
                        qnyjExcelUploadErrors.setCode(info.get("excelCode").toString());
                        errorList.add(qnyjExcelUploadErrors);
                        //excelUploadErrorService.insertBatchErrorList(errorList);
                        num = 1;
                        PlatoBasicResult result = PlatoResult.successResult(errorList);
                        result.setMessage("文件上传失败,共上传了" + total +"条数据,其中成功"+(total-num)+"条数据，失败"+num+"条数据!");
                        return result;
                    }
                    String time = info.get("excelTime").toString();
                    String[] tmp = time.split("-");
                    if(tmp.length!=3)
                        return null;
                    String excelTime = tmp[0]+tmp[1];
                    List<PlatoExcelFile> excelInfoList = new ArrayList<PlatoExcelFile>();
                    PlatoExcelFile excelInfo = new PlatoExcelFile();

                    try {
                        FileUtil.uploadFile(file.getBytes(), Common.EXCEL_FILE_PATH+bank.getName()+"/"+info.get("excelTime")+"/", fileName);
                    }catch (Exception e){
                        log.info("保存失败："+e.toString());
                        return PlatoResult.failResult("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
                    }
                    excelInfo.setName(info.get("excelName").toString());        //名称
                    excelInfo.setCode(info.get("excelCode").toString());       //标号
                    excelInfo.setBankName(bank.getShortName());
                    excelInfo.setDepartmentId(currentUser.getUserDepId());
                    excelInfo.setBankId(bank.getId());
                    excelInfo.setFileName(fileName);
                    excelInfo.setPath(Common.EXCEL_FILE_PATH+bank.getName()+"/"+info.get("excelTime")+"/"+fileName);
                    excelInfo.setTime(excelTime);
                    excelInfo.setStatus(1);
                    excelInfo.setUpdateTime(DateUtils.getCurrentTime());
                    excelInfo.setUpdateUser(currentUser.getUserRealName());
                    excelInfoList.add(excelInfo);
                    try {
                        excelFileService.insertsNew(excelInfoList);
                    }catch (Exception e){
                        return PlatoResult.failResult(e.toString());
                    }
                }catch (Exception e){
                    String message = "Excel文件解析异常，表【"+e.getMessage()+"】命名不规范，请检查后上传！";
                    return PlatoResult.failResult(message);
                }
            }
        }else{
            return PlatoResult.failResult("上传文件名后缀不符合规定，请检查后上传！");
        }
        log.info("----------------upload end----------------");
        if(num == 0){
            return PlatoResult.successResult("文件上传成功,共上传了" + total +"条数据,其中成功"+(total-num)+"条数据，失败"+num+"条数据！");
        }else{
            //插入上传失败数据
            for(PlatoExcelFile list: noList){
                QnyjExcelUploadError qnyjExcelUploadError = new QnyjExcelUploadError();
                qnyjExcelUploadError.setFailReason(list.getParam1());
                qnyjExcelUploadError.setCode(list.getCode());
                qnyjExcelUploadError.setCreateAuthor(currentUser.getUserName());
                if(list.getFileName().contains("/")){
                    String[] names = list.getFileName().split("\\/");
                    qnyjExcelUploadError.setFileName(names[1].trim());
                }else{
                    qnyjExcelUploadError.setFileName(list.getFileName());
                }
                qnyjExcelUploadError.setCreateTime(DateUtils.format(date,DateUtils.FULL_ST_FORMAT));
                errorList.add(qnyjExcelUploadError);
            }
            //excelUploadErrorService.insertBatchErrorList(errorList);
            PlatoBasicResult result = PlatoResult.successResult(errorList);
            result.setMessage("文件上传部分成功,共上传了" + total +"条数据,其中成功"+(total-num)+"条数据，失败"+num+"条数据!");
            return result;
        }
    }

    /*校验excel文件信息*/
    public Map<String,List<PlatoExcelFile>> ckeckExcelInfo(List<PlatoExcelFile> excelInfos, User currentUser){
        List<PlatoExcelFile> updateList = new ArrayList<>();
        List<PlatoExcelFile> noList = new ArrayList<>();
        Map<String,List<PlatoExcelFile>> excelMap = new HashMap<String,List<PlatoExcelFile>>();
        for (PlatoExcelFile excelInfo : excelInfos) {
            Bank bank = bankService.getByName(excelInfo.getBankName());
            if(null==bank){
                log.info("机构名【"+excelInfo.getBankName()+"】不存在");
                excelInfo.setParam1("机构名【"+excelInfo.getBankName()+"】不存在");
                noList.add(excelInfo);
                continue;
            }
            excelInfo.setBankName(bank.getShortName());
            excelInfo.setBankId(bank.getId());
            excelInfo.setUpdateTime(DateUtils.getCurrentTime());
            excelInfo.setUpdateUser(currentUser.getUserRealName());
            updateList.add(excelInfo);
        }
        excelMap.put("updateList",updateList);
        excelMap.put("noList",noList);
        return excelMap;
    }

    @RequestMapping("/excelUploadInfo")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelUploadInfo(Integer  pageNo,Integer  pageSize){
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        ArrayList<PlatoExcelFile> excelList = excelFileService.selectExcelFileIsStatus ();
        PageInfo<PlatoExcelFile> excelPageInfo = new PageInfo<> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/excelForGenerationInfoNew")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelForGenerationInfoNew(Integer  pageNo,Integer  pageSize , String time){
        log.info("excelForGenerationInfoNew listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        PageHelper.startPage(pageNo,pageSize);
        String time1 = null;
        String times = DateUtils.getData();
        if(time == null || time == ""){
            time1 = times;
        }else{
            time1 =  time;
        }
        List<PlatoExcelFile> excelList = excelFileService.excelForGenerationInfoCountNew(time1,roleId);
        PageInfo<PlatoExcelFile> excelPageInfo = new PageInfo<> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/excelCountUploadInfoNew")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelCountUploadInfo(Integer  pageNo,Integer  pageSize,Integer  bankId,Integer status ,String time){
        log.info("excelCountUploadInfo listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        String times = DateUtils.getData();
        if(time == null || time == ""){
            time = times;
        }
        List<PlatoExcelFile> excelList = excelFileService.excelCountUploadInfo(bankId,status,time);
        PageInfo<PlatoExcelFile> excelPageInfo = new PageInfo<> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/excelUploadGetTime")
    public PlatoBasicResult excelUploadGetTime(){
        List<PlatoExcelFile> excelList = excelFileService.excelUploadByGetTime();
        String time = excelList.get(0).getTime();
        return PlatoResult.successResult(time);
    }

    @RequestMapping("/excelCountUploadInfoNews")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelCountUploadInfos(Integer  pageNo,Integer  pageSize,Integer  bankId , String time){
        log.info("excelCountUploadInfos listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        String times = DateUtils.getData();
        if(time == null || time == ""){
            time = times;
        }
        List<PlatoExcelFile> excelList = excelFileService.excelCountUploadAllInfo(bankId,time);
        PageInfo<PlatoExcelFile> excelPageInfo = new PageInfo<> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/excelUpdateInfo")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelUpdateInfo(Integer  pageNo,Integer  pageSize,Integer  id){
        log.info("excelCountUploadInfos listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        Integer num = excelFileService.excelUpdateInfo(id);
        List<PlatoExcelFile> excelList = excelFileService.excelForGenerationInfoCount(DateUtils.getData());
        PageInfo<PlatoExcelFile> excelPageInfo = new PageInfo<> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/excelErrorInfoList")
    @RequiresPermissions("excel:list")
    public PlatoBasicResult excelErrorInfoList(Integer  pageNo,Integer  pageSize,String  code){
        log.info("excelErrorInfoList listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<QnyjExcelUploadError> excelList = excelUploadErrorService.selectFileErrorList(code);
        PageInfo<QnyjExcelUploadError> excelPageInfo = new PageInfo<QnyjExcelUploadError> (excelList);
        return PlatoResult.successResult(excelPageInfo);
    }

    @RequestMapping("/batchremove")
    public PlatoBasicResult batchremove(String ids){
        excelUploadErrorService.deleteByPrimaryKeyAllExcel(ids);
        return PlatoResult.successResult("批量删除成功！");
    }
}
