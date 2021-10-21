package com.demo.mongodb.service.impl;

import com.demo.mongodb.common.MongodbUtils;
import com.demo.mongodb.domain.FileUploadToMongoInfo;
import com.demo.mongodb.mapper.FileUploadToMongoMapper;
import com.demo.mongodb.service.FileUploadToMongoService;
import com.demo.mongodb.vo.FileUploadToMongoInfoVo;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

@Service
public class FileUploadToMongoServiceImpl implements FileUploadToMongoService {
    @Autowired
    private MongodbUtils mongodbUtils;

    @Autowired
    private FileUploadToMongoMapper fileUploadToMongoMapper;

    @Override
    public Map<String, Object> uploadFile(CommonsMultipartFile[] files, FileUploadToMongoInfoVo fileInfo) {
        String picSuff = "bmp,dib,gif,jfif,jpe,jpeg,jpg,png,tif,tiff,ico";
        Map<String, Object> map = new HashMap<>();
        List<String> ids = new ArrayList();

        for (CommonsMultipartFile addFiles : files) {
            // 获得提交的文件原始名称
            String originalFileName = addFiles.getOriginalFilename();
            // 提交的文件名称
            String fileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            // 获得提交的文件大小
            Long fileSize = addFiles.getSize();
            // 获得提交的文件后缀
            String fileEnd = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            // 临时存储文件
            String tempFilePath = "C:/tempThumImage/";

            try {
                // 获得文件输入流
                InputStream ins = addFiles.getInputStream();
                // 获得文件类型
                String contentType = addFiles.getContentType();
                // 将文件存储到 MongoDB, MongoDB 将会返回这个文件的具体信息
                ObjectId gridFSFile = mongodbUtils.saveFile(ins, originalFileName, contentType);
                // 保存文件信息到数据库中
                FileUploadToMongoInfoVo fileUploadToMongoInfo = new FileUploadToMongoInfoVo();
                String id = UUID.randomUUID().toString();
                fileUploadToMongoInfo.setId(id);
                fileUploadToMongoInfo.setMasterId(fileInfo.getMasterId());
                fileUploadToMongoInfo.setPrefix(fileInfo.getPrefix());
                fileUploadToMongoInfo.setSuffix(fileInfo.getSuffix());
                fileUploadToMongoInfo.setFileType(fileEnd);
                if (fileInfo.getFileName() != null && !"".equals(fileInfo.getFileName().trim()))
                    fileUploadToMongoInfo.setFileName(fileInfo.getFileName());
                else fileUploadToMongoInfo.setFileName(fileName);
                fileUploadToMongoInfo.setFileSize(fileSize);
                fileUploadToMongoInfo.setUploadTime(new Date());
                fileUploadToMongoInfo.setMongoId(gridFSFile.toString());
                // 判断文件是否为图片
                if (picSuff.lastIndexOf(fileEnd) != -1 && fileSize > 300 * 1024) {
                    // 压缩图片
                    String smallFileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")).toLowerCase() + "_small." + fileEnd;
                    double rate = fileSize / (300 * 1024);
                    String smallPath = doCompress(addFiles, rate, 1, smallFileName, tempFilePath);
                    InputStream insSmall = new FileInputStream(smallPath);
                    ObjectId gridFSFileSmall = mongodbUtils.saveFile(insSmall, smallFileName, contentType);
                    fileUploadToMongoInfo.setMongoThumId(gridFSFileSmall.toString());
                } else {
                    fileUploadToMongoInfo.setMongoThumId(gridFSFile.toString());
                }
                fileUploadToMongoInfo.setIsDeleted("0");
                fileUploadToMongoMapper.insertSelective(fileUploadToMongoInfo);

                ids.add(id);
            } catch (Exception e) {
                map.put("code", 500);
                map.put("msg", "上传失败");
                e.printStackTrace();
            } finally {
                if (!StringUtils.isEmpty(tempFilePath)) {
                    File fileTemp = new File(tempFilePath);
                    File[] f = fileTemp.listFiles();
                    if (f != null) {
                        for (File file1 : fileTemp.listFiles()) {
                            file1.delete();
                        }
                    }

                }
            }
        }

        map.put("ids", ids);
        map.put("code", 200);
        map.put("msg", "上传成功");

        return map;
    }

    @Override
    public Map<String, Object> uploadVideo(File[] files, FileUploadToMongoInfoVo videoInfo) {
        Map<String, Object> map = new HashMap<>();
        List<String> ids = new ArrayList();

        for (File file : files) {
            try {
                InputStream is = new FileInputStream(file);
                String name = file.getName();
                // 获得提交的文件后缀
                String fileEnd = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                // 获得提交的文件大小
                Long fileSize = file.length();
                String contentType = new MimetypesFileTypeMap().getContentType(file);

                ObjectId gridFSFile = mongodbUtils.saveFile(is, name, contentType);

                FileUploadToMongoInfoVo fileUploadToMongoInfo = new FileUploadToMongoInfoVo();
                String id = UUID.randomUUID().toString();
                fileUploadToMongoInfo.setId(id);
                fileUploadToMongoInfo.setMasterId(videoInfo.getMasterId());
                fileUploadToMongoInfo.setPrefix(videoInfo.getPrefix());
                fileUploadToMongoInfo.setSuffix(videoInfo.getSuffix());
                fileUploadToMongoInfo.setFileType(fileEnd);
                fileUploadToMongoInfo.setFileName(name);
                fileUploadToMongoInfo.setFileSize(fileSize);
                fileUploadToMongoInfo.setUploadTime(new Date());
                fileUploadToMongoInfo.setMongoId(gridFSFile.toString());
                fileUploadToMongoInfo.setIsDeleted("0");

                fileUploadToMongoMapper.insertSelective(fileUploadToMongoInfo);

                ids.add(id);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        map.put("ids", ids);
        map.put("code", 200);
        map.put("msg", "上传成功");

        return map;
    }

    @Override
    public Map<String, Object> deleteFileByMongoId(FileUploadToMongoInfoVo fileInfo) {
        Map<String, Object> result = new HashMap<>();
        List<String> mongoIds = fileInfo.getMongoIds();

        if (mongoIds != null) {
            fileInfo.setDeletedTime(new Date());
            fileUploadToMongoMapper.deleteFileByMongoId(fileInfo);

            for (String mongoId : mongoIds) {
                mongodbUtils.delFileByFileId(mongoId);

                // 删除缩略图
                String thumid = fileUploadToMongoMapper.getThumidByMongoId(mongoId);
                if (Strings.isNotBlank(thumid)) {
                    mongodbUtils.delFileByFileId(thumid);
                }
            }

            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            result.put("code", 200);
            result.put("msg", "未发现可删除附件");
        }

        return result;
    }

    @Override
    public List<FileUploadToMongoInfo> getFilesByCustomize(FileUploadToMongoInfoVo fileInfo) {
        return fileUploadToMongoMapper.getFilesByCustomize(fileInfo);
    }

    /**
     * 压缩图片方法
     *
     * @param file          将要压缩的图片
     * @param rate          压缩比例
     * @param quality       压缩清晰度, 建议为1.0
     * @param smallFileName 压缩图片后, 添加的扩展名（在图片后缀名前添加）
     * @return 如果处理正确返回压缩后的文件名, 返回 null 则参数可能有误
     */
    private String doCompress(CommonsMultipartFile file, double rate, float quality, String smallFileName, String tempFilePath) {
        if (file != null && rate > 0) {
            Image srcFile = null;
            String newImage = null;

            try {
                // 读取图片信息
                srcFile = ImageIO.read(file.getInputStream());

                // 为等比缩放计算输出的图片宽度及高度
                int new_w = (int) (((double) srcFile.getWidth(null)) / rate);
                int new_h = (int) (((double) srcFile.getHeight(null)) / rate);

                // 设定宽高
                BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
                tag.getGraphics().drawImage(srcFile, 0, 0, new_w, new_h, null);

                // 设定压缩之后图片的临时存放位置
                File fileTemp = new File(tempFilePath);
                if (!fileTemp.exists()) {
                    fileTemp.mkdirs();
                }

                // 设定压缩后的文件名
                newImage = tempFilePath + smallFileName;
                FileOutputStream out = new FileOutputStream(newImage);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);

                // 设定压缩质量
                jep.setQuality(quality, true);
                encoder.encode(tag, jep);

                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                srcFile.flush();
            }
            return newImage;
        } else {
            return null;
        }
    }
}
