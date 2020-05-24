package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.core.basic.service.*;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.user.service.IDepartmentService;
import com.cbrc.plato.user.service.IPermissionService;
import com.cbrc.plato.user.service.IUserService;
import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.checktable.CheckTableUtil;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/checkRule")
public class CheckRuleController {
    @Autowired
    ICheckRuleTableService checkRuleTableService;

    @Autowired
    ICheckRuleInfoService checkRuleInfoService;

    @Autowired
    IUserService userService;

    @Autowired
    IBankService bankService;

    @Autowired
    IDepartmentService departmentService;

    @Autowired
    IExcelFileService excelFileService;

    @Autowired
    IPermissionService permissionService;

    @RequestMapping("/create")
    public PlatoBasicResult create(String ruleName,String ruleDiscribe,String accuracy,Integer department,String fileName){
        log.info("create Start "+ruleName+","+fileName);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null)
            return PlatoResult.unauthenticatedResult();

        String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId()+"\\rule\\";
        log.info(path+ruleName);
        CheckRuleTable checkRuleTable = CheckTableUtil.createCheckRuleTableWithExcel(path+fileName);
        FileUtil.removeDir(path);
        if(checkRuleTable==null||checkRuleTable.getCheckRuleInfoList().size()==0)
            return PlatoResult.failResult("不存在规则！");
        if(ruleName!=null&&ruleName!=""){
            checkRuleTable.setRuleName(ruleName);
            CheckRuleTable checkRuleTable1 = this.checkRuleTableService.getByRuleNameAndUser(ruleName,currentUser.getId());
            if(checkRuleTable1!=null)
                return PlatoResult.failResult("已存在相同名称的审表规则！请删除原规则或修改规则名称！");
        }

        if(ruleDiscribe!=null&&ruleDiscribe!="")
            checkRuleTable.setRuleDiscribe(ruleDiscribe);
        if(accuracy!=null&&accuracy!="")
            checkRuleTable.setAccuracy(accuracy);
        if(department!=null)
            checkRuleTable.setDepartment(department);

        Timestamp timestamp = DateUtils.getCurrentTimestamp();
        checkRuleTable.setCreateTime(timestamp);
        checkRuleTable.setUser(currentUser.getId());
        checkRuleTable.setStatus(1);
        checkRuleTableService.insert(checkRuleTable);

        CheckRuleTable checkRuleTable1 = this.checkRuleTableService.getByRuleNameAndUser(ruleName,currentUser.getId());
        if(checkRuleTable1==null){
            return PlatoResult.failResult("创建失败！");
        }
        List<CheckRuleInfo> checkRuleInfos = checkRuleTable.getCheckRuleInfoList();
        for(CheckRuleInfo checkRuleInfo:checkRuleInfos){
            checkRuleInfo.setRuleTableId(checkRuleTable1.getId());
        }
        if(checkRuleInfos.size()>0){
            this.checkRuleInfoService.inserts(checkRuleInfos);
        }
        return PlatoResult.successResult();
    }

    @RequestMapping("/listpage")
    public PlatoBasicResult listpage(Integer  pageNo,Integer  pageSize , String serchRuleName){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null){
            return PlatoResult.unauthenticatedResult();
        }
        Integer dpId = currentUser.getUserDepId();

        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<CheckRuleTable> ruleList = checkRuleTableService.getAllByRuleName(serchRuleName);
        for(CheckRuleTable checkRuleTable:ruleList){
            User user = userService.getById(checkRuleTable.getUser());
            checkRuleTable.setUserName(user.getUserRealName());
        }
        PageInfo<CheckRuleTable> userPageInfo = new PageInfo<>(ruleList);
        return PlatoResult.successResult(userPageInfo);
    }

    @RequestMapping("/list")
    public PlatoBasicResult list(){
        List<CheckRuleTable> ruleList = checkRuleTableService.getAllByRuleName("");
        for(CheckRuleTable checkRuleTable:ruleList){
            User user = userService.getById(checkRuleTable.getUser());
            checkRuleTable.setUserName(user.getUserRealName());
        }
        return PlatoResult.successResult(ruleList);
    }

    @RequestMapping("/info/listpage")
    public PlatoBasicResult ruleInfosistpage(Integer  pageNo,Integer  pageSize ,Integer rtId){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null){
            return PlatoResult.unauthenticatedResult();
        }
        Integer dpId = currentUser.getUserDepId();

        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<CheckRuleInfo> checkRuleInfos = checkRuleInfoService.getByRuleTableId(rtId);
        PageInfo<CheckRuleInfo> userPageInfo = new PageInfo<>(checkRuleInfos);
        return PlatoResult.successResult(checkRuleInfos);
    }

    @RequestMapping("/rule/excel")
    public @ResponseBody
    PlatoBasicResult createRuleByExcel(@RequestParam("file") MultipartFile file){
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        String fileName = file.getOriginalFilename();//文件名
        log.info(fileName);
        try {
            FileUtil.uploadFile(file.getBytes(), Common.TEMPORARY_FILE_PATH, fileName);
        }catch (Exception e){
            log.info("保存失败："+e.toString());
            return PlatoResult.failResult("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
        }
        return null;
    }


    @RequestMapping("/local/rule/upload")
    PlatoBasicResult ruleUpload(@RequestParam("file") MultipartFile file){
        String fileName = file.getOriginalFilename();//文件名
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        if(currentUser==null){
            return PlatoResult.unauthenticatedResult();
        }
        String path = Common.CHECK_TEMPORARY_FILE_PATH+"checktable-"+currentUser.getId()+"\\rule\\";
        try {
            FileUtil.uploadFile(file.getBytes(),path, fileName);
        }catch (Exception e){
            log.info("保存失败："+e.toString());
            log.info("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
            return PlatoResult.failResult("excel文件上传保存失败，失败原因【"+e.getMessage()+"】");
        }

        return PlatoResult.successResult("上传成功！");
    }





}
