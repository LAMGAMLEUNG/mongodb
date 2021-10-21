package com.demo.mongodb.vo;

import com.demo.mongodb.domain.FileUploadToMongoInfo;

import java.util.List;

public class FileUploadToMongoInfoVo extends FileUploadToMongoInfo {
    /*MongoId 集合*/
    private List<String> mongoIds;

    public List<String> getMongoIds() {
        return mongoIds;
    }

    public void setMongoIds(List<String> mongoIds) {
        this.mongoIds = mongoIds;
    }
}
