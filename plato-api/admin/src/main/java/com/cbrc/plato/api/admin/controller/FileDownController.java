package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * @author fangtao
 * @date 2019-04-12
 * 文件下载
 */
@Slf4j
@Controller
@RequestMapping("/fileDown")
public class FileDownController {
    private static Logger logger = LoggerFactory.getLogger(FileDownController.class);
    @Autowired
    private IExcelFileService excelFileService;

    /**
     * 多个文件下载压缩为zip，单个文件直接下载为excel
     * billname:文件名
     * filename：文件存放路径
     */
    @RequestMapping(value="/zipfileDownload", method = RequestMethod.GET)
    public @ResponseBody PlatoBasicResult zipfileDownload(@RequestParam("fileIds") String fileIds , HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
        //响应头的设置
        response.reset();
        //判断是下载多个还是单个文件
        boolean status = fileIds.contains(",");
        if(status){
            //设置压缩包的名字
            //解决不同浏览器压缩包名字含有中文时乱码的问题
            HttpSession session = request.getSession();
            Date date=new Date();
            String billname = "Excel-";
            String downloadName = billname + DateUtils.format(date,DateUtils.DAY_FORMAT)+ ".zip";
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            //返回客户端浏览器的版本号、类型
            String agent = request.getHeader("USER-AGENT");
            try {
                //针对IE或者以IE为内核的浏览器：
                if (agent.contains("MSIE") || agent.contains("Trident")) {
                    downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
                } else {
                    //非IE浏览器的处理：
                    downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setHeader("Content-Disposition", "attachment;filename="+ downloadName);
            response.setContentType("application/json;charset=UTF-8");
            //设置压缩流：直接写入response，实现边压缩边下载
            ZipOutputStream zipos = null;
            try {
                zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
                zipos.setMethod(ZipOutputStream.DEFLATED); //设置压缩方法
            } catch (Exception e) {
                return PlatoResult.failResult(e.getMessage());
            }
            //循环将文件写入压缩流
            DataOutputStream os = null;
            List<PlatoExcelFile> platoExcelFileList = excelFileService.findAllFileByIds(fileIds);
            if(platoExcelFileList != null){
                for (PlatoExcelFile platoExcelFile : platoExcelFileList) {
                    String filePath = platoExcelFile.getPath();
                    String fileName = platoExcelFile.getFileName();
                    File file = new File(filePath);
                    if (file.exists()) {
                        try {
                            //添加ZipEntry，并ZipEntry中写入文件流
                            //这里，加上i是防止要下载的文件有重名的导致下载失败
                            zipos.putNextEntry(new ZipEntry(fileName));
                            os = new DataOutputStream(zipos);
                            InputStream is = new FileInputStream(file);
                            byte[] b = new byte[100];
                            int length = 0;
                            while ((length = is.read(b)) != -1) {
                                os.write(b, 0, length);
                            }
                            is.close();
                            zipos.closeEntry();
                        } catch (IOException e) {
                            return PlatoResult.failResult(e.getMessage());
                        }
                    }else{
                        return PlatoResult.failResult("您所勾选的文件"+fileName+"不存在，请联系管理员协助解决！");
                    }
                }
                try {
                    os.flush();
                    os.close();
                    zipos.close();
                } catch (IOException e) {
                    return PlatoResult.failResult(e.getMessage());
                }
                return PlatoResult.successResult("zip文件下载成功！");
            }else{
                return PlatoResult.failResult("您所勾选的文件中有不存在的excel表，请联系管理员核实！");
            }
        }else{
            PlatoExcelFile platoExcelFile = excelFileService.selectByPrimaryKey(Integer.valueOf(fileIds));
            if(platoExcelFile != null){
                return FileUtil.downloadfile(response , request, platoExcelFile.getFileName(), platoExcelFile.getPath());
            }else{
                return PlatoResult.failResult("您所勾选的单个excle文件不存在，请联系管理员核实！");
            }
        }
    }
}