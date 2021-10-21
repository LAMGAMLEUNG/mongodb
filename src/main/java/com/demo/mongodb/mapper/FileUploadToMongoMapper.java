package com.demo.mongodb.mapper;

import com.demo.mongodb.domain.FileUploadToMongoInfo;
import com.demo.mongodb.vo.FileUploadToMongoInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileUploadToMongoMapper {
    int insertSelective(FileUploadToMongoInfoVo fileInfo);

    int deleteFileByMongoId(FileUploadToMongoInfoVo fileInfo);

    String getThumidByMongoId(String mongoId);

    List<FileUploadToMongoInfo> getFilesByCustomize(FileUploadToMongoInfoVo fileInfo);
}
