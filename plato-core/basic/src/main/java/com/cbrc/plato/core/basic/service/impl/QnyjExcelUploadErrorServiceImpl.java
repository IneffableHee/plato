package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.QnyjExcelUploadErrorMapper;
import com.cbrc.plato.core.basic.model.QnyjExcelUploadError;
import com.cbrc.plato.core.basic.service.IQnyjExcelUploadErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QnyjExcelUploadErrorServiceImpl implements IQnyjExcelUploadErrorService {

    @Autowired
    private QnyjExcelUploadErrorMapper qnyjExcelUploadErrorMapper;
    @Override
    public List<QnyjExcelUploadError> selectFileErrorList(String code) {
        return qnyjExcelUploadErrorMapper.selectFileErrorList(code);
    }

    @Override
    public int insertSelective(QnyjExcelUploadError record) {
        return qnyjExcelUploadErrorMapper.insertSelective(record);
    }

    @Override
    public int deleteByPrimaryKeyAllExcel(String ids) {
        return qnyjExcelUploadErrorMapper.deleteByPrimaryKeyAllExcel(ids);
    }

    @Override
    public int insertBatchErrorList(List<QnyjExcelUploadError> list) {
        return qnyjExcelUploadErrorMapper.insertBatchErrorList(list);
    }
}
