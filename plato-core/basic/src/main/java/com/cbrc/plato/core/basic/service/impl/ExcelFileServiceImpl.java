package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.ExcelFileMapper;
import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.service.IExcelFileService;
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
public class ExcelFileServiceImpl implements IExcelFileService {
    @Autowired
    ExcelFileMapper excelFileMapper;

    @Override
    public void updates(List<PlatoExcelFile> platoExcelFileList) {
        List<PlatoExcelFile> updateList = new ArrayList<>();
        List<PlatoExcelFile> insertList = new ArrayList<>();

        for(PlatoExcelFile platoExcelFile : platoExcelFileList){
            PlatoExcelFile oldFile = getByBankIdCodeAndTime(platoExcelFile.getBankId(), platoExcelFile.getCode(), platoExcelFile.getTime());
            if(null!=oldFile){
                platoExcelFile.setId(oldFile.getId());
                updateList.add(platoExcelFile);
            }else {
                insertList.add(platoExcelFile);
            }
        }
        if(insertList.size()>0){
            log.info("insert :"+insertList.size());
            excelFileMapper.inserts(insertList);
        }

        if(updateList.size()>0){
            log.info("update :"+updateList.size());
            excelFileMapper.updates(updateList);
        }
    }

    @Override
    public int insertsNew(List<PlatoExcelFile> platoExcelFileList) {
        return excelFileMapper.insertsNew(platoExcelFileList);
    }

    @Override
    public int update(PlatoExcelFile platoExcelFile) {
        PlatoExcelFile oldFile = getByBankIdCodeAndTime(platoExcelFile.getBankId(), platoExcelFile.getCode(), platoExcelFile.getTime());
        if(null!=oldFile){
            platoExcelFile.setId(oldFile.getId());
            return excelFileMapper.updateByPrimaryKeySelective(platoExcelFile);
        }else {
            return excelFileMapper.insertSelective(platoExcelFile);
        }
    }

    @Override
    public PlatoExcelFile getByBankIdCodeAndTime(int bid, String code, String time) {
        return excelFileMapper.selectByBankIdCodeAndTime(bid,code,time);
    }

    @Override
    public List<PlatoExcelFile> getByBankNameAndTime(String bankName, String time) {
        return excelFileMapper.selectByBankNameAndTime(bankName,time);
    }

    @Override
    public PlatoBasicResult upLoadZipFile(MultipartFile file, String filename) throws IOException {
        log.info("接收到上传文件:" + file.getOriginalFilename() +",文件上传开始");
        try {
            FileUtil.uploadFile(file.getBytes(), Common.EXCEL_FILE_ZIP_PATH,filename);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            return PlatoResult.failResult("文件上传失败！");
        }
        log.info("接收到上传文件:" + file.getOriginalFilename() + ",文件上传结束");

        ZipFile zip = new ZipFile(Common.EXCEL_FILE_ZIP_PATH+filename, "GBK");
        List<PlatoExcelFile> excelInfos = new ArrayList<>();
        Enumeration<? extends ZipEntry> entries = zip.getEntries();
        List<String> directory = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            // 如果是文件夹，获取文件夹名称
            if (entry.isDirectory()) {
//                log.info("*****文件夹:"+entry.getName()+"-"+entry.getName().length());
                directory.add(entry.getName());
            } else {
                String excelName = null;
                if(directory.size()>0){
                    for(String fpath :directory){
                        if(entry.getName().indexOf(fpath)!=-1)
                            excelName = entry.getName().replaceAll(fpath,"");
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

                    //村镇银行有两张G04，系统只用G04利润表，故需屏蔽G04附注利润表
                    if(info.get("bankName").toString().contains("村镇银行")&&info.get("excelCode").toString().equals("G04")&&info.get("excelName").toString().contains("附注利润表"))
                        continue;

                    String outPath = Common.EXCEL_FILE_PATH + info.get("bankName")+"/"+info.get("excelTime")+"/"+excelName;
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
                    String time = info.get("excelTime").toString();
                    String[] tmp = time.split("-");
                    if(tmp.length!=3)
                        return PlatoResult.failResult("文件名【"+excelName+"】"+"不规范,请核实后再上传!");
                    PlatoExcelFile excelInfo = new PlatoExcelFile();
                    excelInfo.setFileName(excelName);
                    excelInfo.setPath(outPath);
                    excelInfo.setTime(tmp[0]+tmp[1]);
                    excelInfo.setBankName(info.get("bankName").toString());
                    excelInfo.setCode(info.get("excelCode").toString());       //标号
                    excelInfo.setName(info.get("excelName").toString());        //名称
                    excelInfo.setStatus(1);
                    excelInfos.add(excelInfo);
                    in.close();
                    out.close();
                }else{
                    log.info("文件"+entry.getName()+"不是Excel文件");
                    PlatoExcelFile excelInfo = new PlatoExcelFile();
                    excelInfo.setFileName(entry.getName());
                    excelInfos.add(excelInfo);
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
    public List<PlatoExcelFile> findAllFileByIds(String Ids) {
        return excelFileMapper.findAllFileByIds(Ids);
    }

    @Override
    public PlatoExcelFile selectByPrimaryKey(Integer id) {
        return excelFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public ArrayList<PlatoExcelFile> selectExcelFileIsStatus() {
        return excelFileMapper.selectExcelFileIsStatus ();
    }

    @Override
    public List<PlatoExcelFile> excelForGenerationInfoCountNew(String time , Integer roleId) {
        return excelFileMapper.excelForGenerationInfoCountNew(time,roleId);
    }

    @Override
    public List<PlatoExcelFile> excelForGenerationInfoCount(String time) {
        return excelFileMapper.excelForGenerationInfoCount(time);
    }

    //    excelForGenerationInfo() 得到统计信息，以下获得上传或是未上传的详细数据
    @Override
    public ArrayList<PlatoExcelFile> excelCountUploadInfo(Integer  bankId, Integer status, String time) {
        return excelFileMapper.excelCountUploadInfo (   bankId,  status,time);
    }

    @Override
    public List<PlatoExcelFile> excelCountUploadAllInfo(Integer bankId, String time) {
        return excelFileMapper.excelCountUploadAllInfo(bankId,time);
    }

    @Override
    public List<PlatoExcelFile> excelUploadByGetTime() {
        return excelFileMapper.excelUploadByGetTime();
    }

    @Override
    public List<String> getExcelTimeList() {
        return excelFileMapper.selectTimeList();
    }

    @Override
    public List<String> getBankTimeList(String bankName) {
        return excelFileMapper.selectBankTimeList(bankName);
    }

    @Override
    public List<String> getBankList() {
        return excelFileMapper.selectBankList();
    }

    @Override
    public int excelUpdateInfo(Integer id) {
        return excelFileMapper.excelUpdateInfo(id);
    }

}
