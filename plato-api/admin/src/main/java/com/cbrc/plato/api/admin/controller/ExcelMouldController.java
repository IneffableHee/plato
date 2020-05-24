package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.core.basic.model.Bank;
import com.cbrc.plato.core.basic.model.BankGroup;
import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.model.ExcelMould;
import com.cbrc.plato.core.basic.service.IBankGroupService;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.core.basic.service.IExcelMouldService;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.response.PlatoResultCodeEnum;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mould")
public class ExcelMouldController {

    @Autowired
    IBankService bankService;

    @Autowired
    IBankGroupService bankGroupService;

    @Autowired
    IExcelMouldService excelMouldService;

    @Autowired
    IExcelFileService excelFileService;

    @RequestMapping(value="/upload",method = RequestMethod.POST)
    @RequiresPermissions("mould:upload")
    public @ResponseBody PlatoBasicResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        log.info("----------------"+currentUser.getUserName()+"upload----------------");
        //先将获取到的文件上传到临时文件夹
        String fileName = file.getOriginalFilename();//文件名
        int total = 0;//上传总的文件数
        Date date = DateUtils.getCurrentTime();
        if(StringUtil.isEmpty(fileName)){
            return PlatoResult.failResult("上传文件名为空，请检查文件重新上传!");
        }
        String postfix = FileUtil.getPostfix(fileName);
        if(StringUtil.isEmpty(postfix)) {
            return PlatoResult.failResult("上传文件后缀名为空，请检查文件重新上传!");
        }
        if(Common.zip_POSTFIX.equals(postfix)){
            try {
                Map mouldZipInfo = FileUtil.parseZipMould(StringUtils.substringBefore(fileName, "."));
                if (mouldZipInfo == null){
                    return PlatoResult.failResult("上传文件不符合压缩文件模板命名规则，请检查后重试!");
                }
                if(!checkMouldZip(mouldZipInfo)){
                    return PlatoResult.failResult("上传压缩文件机构类型错误，请检查后重试!");
                }
                //解压zip文件
                mouldZipInfo.put("createTime",date);
                String saveName = DateUtils.format(date,DateUtils.DATA_FORMAT)+"_"+currentUser.getUserRealName()+"_"+fileName;
                PlatoBasicResult saveResult = excelMouldService.upLoadZipMould(file,mouldZipInfo,saveName);
                if(saveResult.getCode() == PlatoResultCodeEnum.FAIL){
                    return saveResult;
                }
                List<ExcelMould> excelMouldList = (List<ExcelMould>) saveResult.getData();

                BankGroup group = bankGroupService.getByName(mouldZipInfo.get("groupName").toString());
                if(group.getStatistic() == 1) {
                    for (ExcelMould excelInfo : excelMouldList) {
                        if (!excelInfo.getBgName().equals(group.getStatisticName())){
                            return PlatoResult.failResult("汇总Excel表：【" + excelInfo.getBgName() + excelInfo.getExcelCode() + excelInfo.getFileName() + "】机构名不正确");
                        }
                        excelInfo.setBgId(group.getId());
                        excelInfo.setBgName(group.getName());
                        excelInfo.setDepartmentId(currentUser.getUserDepId());
                        excelInfo.setAuthor(currentUser.getUserRealName());
                        excelInfo.setAuthorId(currentUser.getId());
                    }
                }else{
                    for (ExcelMould excelInfo : excelMouldList) {
                        Bank child = bankService.getByName(excelInfo.getBgName());
                        if (null == child) {
                            return PlatoResult.failResult("Excel表：【" + excelInfo.getFileName() + "】机构名：【" + excelInfo.getBgName() + "】不正确。");
                        }
                        if(child.getGroupId()!= group.getId() && group.getLevel().indexOf(child.getGroupId().toString()) == -1){
                            return PlatoResult.failResult("Excel表：【" + excelInfo.getFileName() + "】机构名：【" + excelInfo.getBgName() + "】不属于压缩包机构类型：【" + mouldZipInfo.get("groupName")+"】");
                        }
                        excelInfo.setBgId(group.getId());
                        excelInfo.setBgName(group.getName());
                        excelInfo.setDepartmentId(currentUser.getUserDepId());
                        excelInfo.setAuthor(currentUser.getUserRealName());
                        excelInfo.setAuthorId(currentUser.getId());
                    }
                }
                /*if(ckeckExcelInfo(excelMouldList,mouldZipInfo,currentUser)==null){
                    return PlatoResult.failResult("表错误，请检查后重试!");
                }*/
                total = excelMouldList.size();
                excelMouldService.updates(excelMouldList);
            }catch (Exception e){
                String message = "Zip压缩包解析异常，压缩包内表【"+e.getMessage()+"】命名不规范，请检查后上传！";
                return PlatoResult.failResult(message);
            }
        }else if(Common.OFFICE_EXCEL_2010_POSTFIX.equals(postfix) || Common.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
            String type = "年报";
            try {
                //获取文件类型（月报、季报、年报）
                Map info = FileUtil.parseExcel(fileName);
                Bank excelBank = bankService.getByName(info.get("bankName").toString());
                if(null == excelBank){
                    return PlatoResult.failResult("上传文件表机构不存在，请检查后重新上传！");
                }
                BankGroup bankGroup = bankGroupService.getById(excelBank.getGroupId());
                if(null == bankGroup){
                    return PlatoResult.failResult("上传文件表机构不存在上级模板机构，请检查后重新上传！");
                }
                try {
                    FileUtil.uploadFile(file.getBytes(), Common.EXCEL_MOULD_PATH+bankGroup.getName()+"/dange/", fileName);
                }catch (Exception e){
                    return PlatoResult.failResult("文件保存失败，请检查上传路径！");
                }
                ExcelMould excelMould = new ExcelMould();
                excelMould.setDepartmentId(currentUser.getUserDepId());
                excelMould.setExcelType(type);
                excelMould.setExcelName(info.get("excelName").toString());
                excelMould.setExcelCode(info.get("excelCode").toString());
                excelMould.setBgName(bankGroup.getName());
                excelMould.setBgId(bankGroup.getId());
                excelMould.setFilePath(Common.EXCEL_MOULD_PATH+"dange/"+fileName);
                excelMould.setFileName(fileName);
                excelMould.setAuthor(currentUser.getUserRealName());
                excelMould.setAuthorId(currentUser.getId());
                excelMould.setCreateTime(DateUtils.getCurrentTime());
                excelMould.setStatus(1);
                total = 1;
                excelMouldService.update(excelMould);
            }catch (Exception e){
                String message = "Excel文件解析异常，表【"+e.getMessage()+"】命名不规范，请检查后上传！";
                return PlatoResult.failResult(message);
            }
        }else{
            return PlatoResult.failResult("上传文件命名不符合规定，请检查后上传！");
        }
        return PlatoResult.successResult("文件上传校验成功，共上传了"+total+"条数据！");
    }


    @RequestMapping("/effect")
    public PlatoBasicResult effect(String last,boolean quarter) throws Exception{
        List<BankGroup> bankGroupList = bankGroupService.getBankGroupList();
        List<ExcelMould> excelMouldList = new ArrayList<>();
        if(quarter){
            excelMouldList = excelMouldService.getAllMouldList();
        }else {
            excelMouldList = excelMouldService.getMouthMouldList();
        }

        log.info("bankGroupList:"+bankGroupList.size()+",excelMouldList:"+excelMouldList.size()+",bankList:");
        List<String> groups = new ArrayList<>();
        List<PlatoExcelFile> platoExcelFileList = new ArrayList<>();

        for(ExcelMould excelMould:excelMouldList){
//            log.info(excelMould.getBgName()+","+excelMould.getExcelCode());
            List<Bank> bankList = bankService.getBankByGroupId(excelMould.getBgId());
            for(Bank bank:bankList){
                PlatoExcelFile platoExcelFile = new PlatoExcelFile();
                platoExcelFile.setBankId(bank.getId());
                platoExcelFile.setBankName(bank.getShortName());
                platoExcelFile.setType(excelMould.getExcelType());
                platoExcelFile.setCode(excelMould.getExcelCode());
                platoExcelFile.setName(excelMould.getExcelName());
                platoExcelFile.setDepartmentId(excelMould.getDepartmentId());
                platoExcelFile.setTime(last);
                platoExcelFileList.add(platoExcelFile);
//                log.info("+--"+bank.getName()+","+excelMould.getExcelCode());
            }
        }
        log.info("-----platoExcelFileList size:"+ platoExcelFileList.size());
        long startTime = System.currentTimeMillis();
        excelFileService.updates(platoExcelFileList);
        long endTime = System.currentTimeMillis();
        log.info("程序运行时间：" + (endTime - startTime) + "ms");
        return null;
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @RequiresPermissions("mould:edit")
    public PlatoBasicResult edit(ExcelMould mould){
        excelMouldService.update(mould);
        return PlatoResult.successResult();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("mould:delete")
    public PlatoBasicResult delete(int id){
        excelMouldService.updateByPrimaryKeys(id);
        return PlatoResult.successResult();
    }


    @RequestMapping("/listpage")
    @RequiresPermissions("mould:list")
    public PlatoBasicResult listpage(Integer  pageNo,Integer  pageSize,String code) throws Exception{
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<ExcelMould> mouldList = excelMouldService.selectAlls(code);
        PageInfo<ExcelMould> mouldPageInfo = new PageInfo<>(mouldList);
        return PlatoResult.successResult(mouldPageInfo);
    }

    @RequestMapping("/list")
    @RequiresPermissions("mould:list")
    public PlatoBasicResult list(){
        List<ExcelMould> moudList = excelMouldService.getAllMouldList();
        return PlatoResult.successResult(moudList);
    }


    public boolean checkMouldZip(Map mouldInfo){
        BankGroup group = bankGroupService.getByName(mouldInfo.get("groupName").toString());
        if (null == group){
            return false;
        }else{
            mouldInfo.put("bankGroup",group.getName());
            return true;
        }
    }

    /*校验excel文件信息*/
    public List<ExcelMould> ckeckExcelInfo(List<ExcelMould> excelInfos,Map mouldInfo,User currentUser){
        BankGroup group = bankGroupService.getByName(mouldInfo.get("groupName").toString());
        if(group.getStatistic() == 1) {
            for (ExcelMould excelInfo : excelInfos) {
                if (!excelInfo.getBgName().equals(group.getStatisticName())){
                    log.info("汇总Excel表：'" + excelInfo.getBgName() + excelInfo.getExcelCode() + excelInfo.getFileName() + "'机构不正确");
                    return null;
                }
                excelInfo.setBgId(group.getId());
                excelInfo.setBgName(group.getName());
                excelInfo.setDepartmentId(currentUser.getUserDepId());
                excelInfo.setAuthor(currentUser.getUserRealName());
                excelInfo.setAuthorId(currentUser.getId());
            }
        }else{
            for (ExcelMould excelInfo : excelInfos) {
                Bank child = bankService.getByName(excelInfo.getBgName());
                if (null == child) {
                    log.info("Excel表：'" + excelInfo.getFileName() + "'机构'" + excelInfo.getBgName() + "'不正确。");
                    return null;
                }
                if(child.getGroupId()!= group.getId() && group.getLevel().indexOf(child.getGroupId().toString()) == -1){
                    log.info("Excel表：'" + excelInfo.getFileName() + "'机构'" + excelInfo.getBgName() + "不属于压缩包机构类型：" + mouldInfo.get("groupName"));
                    return null;
                }
                excelInfo.setBgId(group.getId());
                excelInfo.setBgName(group.getName());
                excelInfo.setDepartmentId(currentUser.getUserDepId());
                excelInfo.setAuthor(currentUser.getUserRealName());
                excelInfo.setAuthorId(currentUser.getId());
            }
        }
        return excelInfos;
    }
}
