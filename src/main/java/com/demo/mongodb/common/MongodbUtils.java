package com.demo.mongodb.common;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;

@Component
public class MongodbUtils {
    @Autowired
    GridFsTemplate gridFsTemplate;

    /**
     * MongoDB 保存文件
     */
    public ObjectId saveFile(InputStream ins, String fileName, String contentType) {
        ObjectId gridFSFile = gridFsTemplate.store(ins, fileName, contentType);

        return gridFSFile;
    }

    /**
     * MongoDB 删除文件
     * @param fileId    文件 ID
     */
    public void delFileByFileId(@RequestParam(name = "fileId") String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));

        // 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);

        gridFsTemplate.delete(query);
    }

    /**
     * MongoDB 获取文件
     * @param fileId    文件 ID
     */
    public String getFileByFileId(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));

        // 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);
        if (gfsfile == null) {
            return "";
        }

        String fileName = gfsfile.getFilename().replace(",", "");

        return fileName;
    }
}
