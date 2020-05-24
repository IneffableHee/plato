package com.cbrc.plato.core.basic.service;

import com.cbrc.plato.core.basic.model.QnyjExcelUploadError;

import java.util.List;

public interface IQnyjExcelUploadErrorService {
    List<QnyjExcelUploadError> selectFileErrorList(String code);

    int insertSelective(QnyjExcelUploadError record);

    int deleteByPrimaryKeyAllExcel(String ids);

    int insertBatchErrorList(List<QnyjExcelUploadError> list);
}
