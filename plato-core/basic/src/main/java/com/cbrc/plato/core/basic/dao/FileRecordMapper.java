package com.cbrc.plato.core.basic.dao;

import com.cbrc.plato.core.basic.model.FileRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordMapper {
    int deleteByPrimaryKey(Integer fileRecordId);

    int insert(FileRecord record);

    int insertSelective(FileRecord record);

    FileRecord selectByPrimaryKey(Integer fileRecordId);

    int updateByPrimaryKeySelective(FileRecord record);

    int updateByPrimaryKey(FileRecord record);
}