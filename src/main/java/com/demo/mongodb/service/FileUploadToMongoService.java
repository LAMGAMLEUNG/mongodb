package com.demo.mongodb.service;

import com.demo.mongodb.domain.FileUploadToMongoInfo;
import com.demo.mongodb.vo.FileUploadToMongoInfoVo;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FileUploadToMongoService {
    Map<String, Object> uploadFile(CommonsMultipartFile[] files, FileUploadToMongoInfoVo fileInfo);

    Map<String, Object> uploadVideo(File[] files, FileUploadToMongoInfoVo videoInfo);

    Map<String,Object> deleteFileByMongoId(FileUploadToMongoInfoVo fileInfo);

    List<FileUploadToMongoInfo> getFilesByCustomize(FileUploadToMongoInfoVo fileInfo);
}
