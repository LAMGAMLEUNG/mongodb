package com.demo.mongodb.controller;

import com.demo.mongodb.domain.FileUploadToMongoInfo;
import com.demo.mongodb.service.FileUploadToMongoService;
import com.demo.mongodb.vo.FileUploadToMongoInfoVo;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fileUtilsMongo")
public class FileUploadToMongoController {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private FileUploadToMongoService fileUploadToMongoService;

    /**
     * 下载文件
     */
    @RequestMapping(value = "/downloadFile")
    public void downloadFile(@RequestParam(name = "file_id") String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Query query = Query.query(Criteria.where("_id").is(fileId));

        // 查询单个文件
        GridFSFile gfsFile = gridFsTemplate.findOne(query);
        if (gfsFile != null) {
            GridFsResource gridFsResource = new GridFsResource(gfsFile, GridFSBuckets.create(mongoDbFactory.getDb()).openDownloadStream(gfsFile.getObjectId()));
            String fileName = gfsFile.getFilename().replace(",", "");

            // 处理中文文件名乱码
            if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                    request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                    || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            // 通知浏览器进行文件下载
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            IOUtils.copy(gridFsResource.getInputStream(), response.getOutputStream());
        }
    }


    /**
     * 下载视频
     */
    @GetMapping(value = "/getVideos")
    public void getVideos(@RequestParam(name = "file_id") ObjectId fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("video/mp4;charset=UTF-8");

        GridFS gridFS = new GridFS(mongoDbFactory.getLegacyDb());
        GridFSDBFile gridFSDBFile = gridFS.findOne(fileId);

        ServletOutputStream outputStream = response.getOutputStream();
        gridFSDBFile.writeTo(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    /**
     * 上传文件
     */
    @RequestMapping(value = "/uploadFile")
    public Map<String, Object> uploadFile(@RequestParam("files") CommonsMultipartFile[] addFiles, FileUploadToMongoInfoVo fileInfo) {
        return fileUploadToMongoService.uploadFile(addFiles, fileInfo);
    }

    /**
     * 上传视频
     */
    @RequestMapping(value = "/uploadVideo")
    public Map<String, Object> uploadVideo(String path, FileUploadToMongoInfoVo videoInfo) {
        File file = new File(path);
        File[] files = file.listFiles();
        return fileUploadToMongoService.uploadVideo(files, videoInfo);
    }

    /**
     * 根据 MongoId 删除文件
     */
    @RequestMapping("/deleteFileByMongoId")
    public Map<String, Object> deleteFileByMongoId(String mongoIds) {
        FileUploadToMongoInfoVo fileInfo = new FileUploadToMongoInfoVo();
        fileInfo.setMongoIds(Arrays.asList(mongoIds.split(",")));
        return fileUploadToMongoService.deleteFileByMongoId(fileInfo);
    }

    /**
     * 自定义查询文件列表
     */
    @RequestMapping("/getFilesByCustomize")
    public List<FileUploadToMongoInfo> getFilesByCustomize(FileUploadToMongoInfoVo fileInfo) {
        return fileUploadToMongoService.getFilesByCustomize(fileInfo);
    }

}
