package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.core.basic.model.ReportMould;
import com.cbrc.plato.core.basic.service.IReportMouldService;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report/mould")
public class ReportMouldController {

    @Autowired
    IReportMouldService reportMouldService;

    @RequestMapping(value="/upload",method = RequestMethod.POST)
    public @ResponseBody
    PlatoBasicResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        log.info("----------------"+currentUser.getUserName()+"upload----------------");
        //先将获取到的文件上传到临时文件夹
        String fileName = file.getOriginalFilename();//文件名
        log.info("----"+fileName);

        if(StringUtil.isEmpty(fileName)){
            log.info("----文件名为空，请检查文件重新上传!");
            return PlatoResult.failResult("文件名为空，请检查文件重新上传!");
        }

        String postfix = FileUtil.getPostfix(fileName);
        if(!Common.OFFICE_EXCEL_2010_POSTFIX.equals(postfix) && !Common.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
            return PlatoResult.failResult("上传文件不符合规定，请上传excel文件！");
        }

        try {
            FileUtil.uploadFile(file.getBytes(), Common.REPORT_MOULD_PATH + currentUser.getUserRealName()+"/", fileName);
        }catch (Exception e){
            log.info("上传失败："+e.toString());
            return PlatoResult.failResult("文件上传失败！");
        }

        ReportMould reportMould = new ReportMould();
        reportMould.setName(fileName.replace("."+postfix,""));
        reportMould.setPath(Common.REPORT_MOULD_PATH + currentUser.getUserRealName()+"/"+fileName);
        reportMould.setAuthor(currentUser.getId());
        reportMould.setCreateTime(DateUtils.getCurrentTime());
        reportMould.setStatus(1);

        try {
            reportMouldService.insert(reportMould);
        } catch (Exception e) {
            e.printStackTrace();
            return PlatoResult.failResult("上传失败，请联系管理员或另保为.xlsx文件后重新上传！");
        }

        return PlatoResult.successResult("上传成功！");
    }

    @RequestMapping("/listpage")
//    @RequiresPermissions("reportmould:list")
    public PlatoBasicResult listpage(Integer  pageNo,Integer  pageSize,String code) throws Exception{
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<ReportMould> mouldList = reportMouldService.getAll();
        PageInfo<ReportMould> mouldPageInfo = new PageInfo<>(mouldList);
        return PlatoResult.successResult(mouldPageInfo);
    }

    @RequestMapping("/userList")
//    @RequiresPermissions("reportmould:list")
    public PlatoBasicResult userList(Integer  pageNo,Integer  pageSize,String code) throws Exception{
        log.info("listpage , pageNo:{} , pageSize:{} .",pageNo,pageSize);
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        PageHelper.startPage(pageNo,pageSize);
        List<ReportMould> mouldList = reportMouldService.getByUserId(currentUser.getId());
        PageInfo<ReportMould> mouldPageInfo = new PageInfo<>(mouldList);
        return PlatoResult.successResult(mouldPageInfo);
    }

    @RequestMapping("/delete")
    @RequiresPermissions("reportmould:delete")
    public PlatoBasicResult delete(Integer id){
        this.reportMouldService.deleteById(id);
        return PlatoResult.successResult();
    }
}
