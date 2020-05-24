package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.ExcelMould;
import com.cbrc.plato.util.response.PlatoBasicResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IExcelMouldService {

    void insert(ExcelMould mould);

    int updateByPrimaryKeys(Integer id);

//    void inserts(List<ExcelMould> item);

    void update(ExcelMould mould);

    int deleteById(int mid);

    List<ExcelMould> getAllMouldList();

    List<ExcelMould> selectAlls(String code);

    List<ExcelMould> getMouthMouldList();

    List<ExcelMould> getQuarterMouldList();

    PlatoBasicResult upLoadZipMould(MultipartFile file, Map mouldZipInfo, String filename) throws IOException;

    void updates(List<ExcelMould> excelMoulds);
}
