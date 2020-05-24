package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ExcelFileMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PlatoExcelFile record);

    int insertSelective(PlatoExcelFile record);

    PlatoExcelFile selectByPrimaryKey(Integer id);

    PlatoExcelFile selectByBankIdCodeAndTime(@Param("bid") int bid, @Param("code") String code, @Param("time") String time);

    List<PlatoExcelFile> selectByBankNameAndTime(@Param("bankName") String bankName,@Param("time") String time);

    int updates(List<PlatoExcelFile> item);

    int insertsNew(List<PlatoExcelFile> platoExcelFileList);

    int inserts(List<PlatoExcelFile> item);

    int updateByPrimaryKeySelective(PlatoExcelFile record);

    int updateByPrimaryKey(PlatoExcelFile record);

    List<PlatoExcelFile> findAllFileByIds(@Param("ids")String Ids);

    ArrayList selectExcelFileIsStatus();

    // Excel 上传后成功与未上传的统计 newCount
    List<PlatoExcelFile> excelForGenerationInfoCountNew(@Param("time")String time, @Param("roleId")Integer roleId);

    // Excel 上传后成功与未上传的统计 newCount
    List<PlatoExcelFile> excelForGenerationInfoCount(@Param("time")String time);

//    excelForGenerationInfo() 得到统计信息，以下获得上传或是未上传的详细数据
    ArrayList excelCountUploadInfo(Integer  bankId,Integer status ,String time);

    // Excel 上传后成功与未上传的统计 newCount
    List<PlatoExcelFile> excelCountUploadAllInfo(Integer  bankId , String time);

    List<PlatoExcelFile> excelUploadByGetTime();

    List<String> selectTimeList();

    List<String> selectBankList();

    List<String> selectBankTimeList(String bankName);

    int excelUpdateInfo(Integer id);
}
