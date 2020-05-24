package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.ExcelMouldMapper;
import com.cbrc.plato.core.basic.model.ExcelMould;
import com.cbrc.plato.core.basic.service.IExcelMouldService;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.utilpo.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public class ExcelMouldServiceImpl implements IExcelMouldService {

    @Autowired
    private ExcelMouldMapper mouldMapper;

    @Override
    public void insert(ExcelMould mould) {
        this.mouldMapper.insertSelective(mould);
    }

    @Override
    public int updateByPrimaryKeys(Integer id) {
        return this.mouldMapper.updateByPrimaryKeys(id);
    }

//    @Override
//    public void inserts(List<ExcelMould> item) {
//        this.mouldMapper.inserts(item);
//    }

    @Override
    public void update(ExcelMould mould) {
        ExcelMould oldMould = this.mouldMapper.selectByCodeAndBGName(mould.getExcelCode(),mould.getBgName());
        if(oldMould!=null){
                log.info("重复条目");
            mould.setId(oldMould.getId());
            this.mouldMapper.updateByPrimaryKeySelective(mould);
        }else{
                log.info("insertSelective");
            this.mouldMapper.insertSelective(mould);
        }
        this.mouldMapper.updateByPrimaryKeySelective(mould);
    }

    @Override
    public int deleteById(int mid){
        return this.mouldMapper.deleteByPrimaryKey(mid);
    }

    @Override
    public PlatoBasicResult upLoadZipMould(MultipartFile file,Map mouldZipInfo,String filename) throws IOException{
        log.info("接收到上传文件:" + file.getOriginalFilename() +",文件上传开始");
        try {
            FileUtil.uploadFile(file.getBytes(),Common.EXCEL_MOULD_ZIP_PATH,filename);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            return PlatoResult.failResult("文件上传失败！");
        }
        log.info("接收到上传文件:" + file.getOriginalFilename() + ",文件上传结束");
        ZipFile zip = new ZipFile(Common.EXCEL_MOULD_ZIP_PATH+filename, "GBK");
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
                    String outPath = Common.EXCEL_MOULD_PATH + mouldZipInfo.get("bankGroup")+"/"+mouldZipInfo.get("mouldType")+"/"+excelName;
                    File targetFile = new File( outPath);
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    //输出文件路径信息
                    InputStream in = zip.getInputStream(entry);
                    OutputStream out = new FileOutputStream(outPath);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while((len=in.read(buf1))>0){
                        out.write(buf1,0,len);
                    }
                    log.info("excelInfo:"+info.toString());
                    ExcelMould excelInfo = new ExcelMould();
                    excelInfo.setFileName(entry.getName());
                    excelInfo.setFilePath(outPath);
                    excelInfo.setCreateTime((Date) mouldZipInfo.get("createTime"));
                    excelInfo.setBgName(info.get("bankName").toString());
                    excelInfo.setExcelCode(info.get("excelCode").toString());       //标号
                    excelInfo.setExcelName(info.get("excelName").toString());        //名称
                    excelInfo.setExcelType(mouldZipInfo.get("mouldType").toString());
                    excelInfo.setStatus(1);
                    excelInfos.add(excelInfo);
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
        return  PlatoResult.successResult(excelInfos);
    }

    @Override
    public void updates(List<ExcelMould> excelMoulds) {
        for (ExcelMould excelMould : excelMoulds){
            this.update(excelMould);
        }
    }

    @Override
    public List<ExcelMould> getAllMouldList(){
        return this.mouldMapper.selectAll();
    }

    @Override
    public List<ExcelMould> selectAlls(String code) {
        return this.mouldMapper.selectAlls(code);
    }


    /*月报*/
    @Override
    public List<ExcelMould> getMouthMouldList() {
        return this.mouldMapper.selectMouth();
    }

    /*季报*/
    @Override
    public List<ExcelMould> getQuarterMouldList() {
        return this.mouldMapper.selectQuarter();
    }
}
