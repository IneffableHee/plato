package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.util.response.PlatoBasicResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface IExcelFileService {

    void updates(List<PlatoExcelFile> platoExcelFileList);

    int insertsNew(List<PlatoExcelFile> platoExcelFileList);

    int update(PlatoExcelFile platoExcelFile);

    PlatoExcelFile getByBankIdCodeAndTime(int bid, String code, String time);

    List<PlatoExcelFile> getByBankNameAndTime(String bankName, String time);

    PlatoBasicResult upLoadZipFile(MultipartFile file, String filename) throws IOException;

    List<PlatoExcelFile> findAllFileByIds(String Ids);

    PlatoExcelFile selectByPrimaryKey(Integer id);

    ArrayList<PlatoExcelFile> selectExcelFileIsStatus();

    // Excel 上传后成功与未上传的统计 newCount
    List<PlatoExcelFile> excelForGenerationInfoCountNew(String time , Integer roleId);

    List<PlatoExcelFile> excelForGenerationInfoCount(String time);

    //    excelForGenerationInfo() 得到统计信息，以下获得上传或是未上传的详细数据
    ArrayList<PlatoExcelFile>  excelCountUploadInfo(Integer  bankId, Integer status, String time);

    List<PlatoExcelFile> excelCountUploadAllInfo(Integer  bankId, String time);

    List<PlatoExcelFile> excelUploadByGetTime();

    //获取时间列表
    List<String> getExcelTimeList();

    List<String> getBankTimeList(String bankName);

    List<String> getBankList();

    int excelUpdateInfo(Integer id);
}
