package com.cbrc.plato.api.admin.controller;

import com.alibaba.fastjson.JSON;
import com.cbrc.plato.core.basic.model.DataInfo;
import com.cbrc.plato.core.basic.service.IDataInfoService;
import com.cbrc.plato.user.model.User;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.fileutil.ReadExcel;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/dataInfoInsert")
public class DataInfoInsertController {
    private static Logger logger = LoggerFactory.getLogger(DataInfoInsertController.class);
    @Autowired
    IDataInfoService iDataInfoService;

    @RequestMapping(value="/excelJson", method = RequestMethod.POST)
    //@RequiresPermissions("excelJson:list")
    public PlatoBasicResult excelJson(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        log.info("----------------"+currentUser.getUserName()+"excelJson----------------");
        //先将获取到的文件上传到临时文件夹
        String fileName = file.getOriginalFilename();//文件名
        String path = Common.DATA_INFO_PATH_C;
        try{
            try {
                FileUtil.uploadFile(file.getBytes(),path, fileName);
            }catch (Exception e) {
                return PlatoResult.failResult("文件保存失败，请检查上传路径！");
            }
            List<Map<String,Object>> list =  ReadExcel.excelToJson(path+fileName);
            String strArray = new Gson().toJson(list);
            log.info(strArray);
            List<DataInfo>  listss = JSON.parseArray(strArray, DataInfo.class);
            iDataInfoService.insertBatchList(listss);
        }catch (Exception e){
            log.info("文件解析异常，请检查文件内容格式！错误日志：【"+e.getMessage().toString()+"】");
            return PlatoResult.failResult("文件解析异常，请检查文件内容格式后重新上传！");
        }
        return PlatoResult.successResult("取数规则表数据插入成功！");
    }

    @RequestMapping(value="/excelDown",method = RequestMethod.POST)
   // @RequiresPermissions("excelJson:down")
    public @ResponseBody PlatoBasicResult excelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String savePath = null;
        String fileName = null;
        OutputStream fileOut = null;
        Workbook wb = null;
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        int roleId = currentUser.getUserRoleId();
        String groupId = request.getParameter("groupId");
        try {
            wb = new XSSFWorkbook();
            //标题行抽出字段
            String[] title = {"机构组","数据来源表编号", "数据名称", "取数单元格", "表类型", "唯一编码", "上级编码（后置条件）"};
            //设置sheet名称，并创建新的sheet对象
            String sheetName = "取数规则表";
            Sheet stuSheet = wb.createSheet(sheetName);
            //获取表头行
            Row titleRow = stuSheet.createRow(0);
            //创建单元格，设置style居中，字体，单元格大小等
            CellStyle style = wb.createCellStyle();
            Cell cell = null;
            //把已经写好的标题行写入excel文件中
            for (int i = 0; i < title.length; i++) {
                cell = titleRow.createCell(i);
                cell.setCellValue(title[i]);
                cell.setCellStyle(style);
            }
            //把从数据库中取得的数据一一写入excel文件中
            Row row = null;
            List<DataInfo> dataInfoList = new ArrayList<DataInfo>();
            if(groupId == null || groupId.equals("")){
                dataInfoList =  iDataInfoService.getDataInfoList(roleId);
            }else{
                dataInfoList =  iDataInfoService.getDataInfoListByGroupId(Integer.parseInt(groupId),roleId);
            }
            if(dataInfoList != null){
                if(dataInfoList.size() == 0){
                    return PlatoResult.failResult("当前选中的机构组无数据！");
                }
                for (int i = 0; i < dataInfoList.size(); i++) {
                    //创建list.size()行数据
                    row = stuSheet.createRow(i + 1);
                    //把值一一写进单元格里
                    row.createCell(0).setCellValue(dataInfoList.get(i).getGroupId());
                    row.createCell(1).setCellValue(dataInfoList.get(i).getExcelCode());
                    row.createCell(2).setCellValue(dataInfoList.get(i).getDataName());
                    row.createCell(3).setCellValue(dataInfoList.get(i).getDataSource());
                    row.createCell(4).setCellValue(dataInfoList.get(i).getDataType());
                    row.createCell(5).setCellValue(dataInfoList.get(i).getOnlyCode());
                    row.createCell(6).setCellValue(dataInfoList.get(i).getParent());
                }
                //设置单元格宽度自适应，在此基础上把宽度调至1.5倍
                for (int i = 0; i < title.length; i++) {
                    stuSheet.autoSizeColumn(i, true);
                    stuSheet.setColumnWidth(i, stuSheet.getColumnWidth(i) * 15 / 10);
                }
                //获取配置文件中保存对应excel文件的路径，本地也可以直接写成F：excel/stuInfoExcel路径
                String folderPath = Common.DATA_INFO_PATH;
                //创建上传文件目录
                File folder = new File(folderPath);
                //如果文件夹不存在创建对应的文件夹
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                //设置文件名
                fileName = DateUtils.format(DateUtils.getCurrentTime(),DateUtils.DATA_FORMAT) + sheetName + ".xlsx";
                savePath = folderPath + fileName;
                fileOut = new FileOutputStream(savePath);
                wb.write(fileOut);
            }else{
                return PlatoResult.failResult("当前选中的机构组无数据！");
            }
        }catch (Exception e){
            log.info("取数规则表模板下载异常！错误日志：【"+e.getMessage().toString()+"】");
            return PlatoResult.failResult("取数规则表模板下载异常,请联系管理员！");
        }finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            return FileUtil.downloadfile(response , request,fileName,savePath);
    }
}
