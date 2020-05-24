package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.QnyjExcelUploadError;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnyjExcelUploadErrorMapper {
    int insert(QnyjExcelUploadError record);

    int insertSelective(QnyjExcelUploadError record);

    int deleteByPrimaryKeyAllExcel(@Param("ids")String ids);

    int insertBatchErrorList(@Param("list") List<QnyjExcelUploadError> list);

    List<QnyjExcelUploadError> selectFileErrorList(@Param("code")String code);
}