package com.yqg.upload;

import com.alibaba.fastjson.JSON;
import com.yqg.upload.common.Base64Info;
import com.yqg.upload.common.Constants;
import com.yqg.upload.common.FileStorage;
import com.yqg.upload.common.ResultInfo;
import com.yqg.upload.config.UploadConfig;
import com.yqg.upload.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jacob on 2017/11/24.
 */
@Component
public class UploadService {

    @Autowired
    private UploadConfig uploadConfig;

    public Logger logger = LoggerFactory.getLogger(getClass());

    public ResultInfo fileStored(MultipartFile file) {

        ResultInfo info = new ResultInfo();

        String url = fileHandler(file, "", uploadConfig);
        info.setData(url);
        info.setMessage("success");
        info.setCode(Constants.FILE_UPLOAD_SUCCESS);
        return info;
    }

    /**
     * ?????
     *
     * @param file
     * @return
     */

    private String fileHandler(MultipartFile file, String paramStr, UploadConfig uploadConfig) {
        ImgHandle ish;
        if (file != null) {
            ish = new ImgStorageHandle(file, paramStr);
            return handler(ish, uploadConfig);
        }
        return null;
    }

    public ResultInfo fileStored(String params, Base64Info base64) {

        ResultInfo info = new ResultInfo();
        String url = fileHandler(base64, params, uploadConfig);
        info.setData(url);
        info.setCode(Constants.FILE_UPLOAD_FAIL);
        info.setCode(Constants.FILE_UPLOAD_SUCCESS);
        return info;
    }

    private String fileHandler(Base64Info base64, String paramStr, UploadConfig uploadConfig) {

        byte[] bytes = FileUtils.GenerateImage(base64.getBase64Str());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ImgHandle ish= new ImgStorageHandle(inputStream, base64, paramStr);
        return handler(ish, uploadConfig);

    }

    private String handler(ImgHandle ish, UploadConfig uploadConfig) {

        FileStorage info = new FileStorage();

        try {
            ish.handle(uploadConfig);
        } catch (ImgHandleResultException img) {
            info = img.getInfo();
        }
        logger.info("addInfo:" + JSON.toJSONString(info));

        // 从A区 copy图片去 B区
        try {
            String bPath = info.getFileUrl().replace(uploadConfig.getRootPath(),uploadConfig.getRootPathBak());
            logger.info("上传B区图片路径为" + bPath);
            File localFile = new File(bPath);

            //???????,????
            if (!localFile.getParentFile().exists() && !localFile.getParentFile().isDirectory()) {
                logger.info("B区文件夹路径为:{}=========", localFile.getParentFile().getPath());
                localFile.getParentFile().mkdirs();
            }
            copyFile(info.getFileUrl(),bPath);

        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }


        return info.getFileUrl();
    }

    // 将一张图片拷贝到另外一个地方 (IO流)
    public  void copyFile(String srcPath, String destPath) throws IOException {
        // 打开输入流
        FileInputStream fis = new FileInputStream(srcPath);
        // 打开输出流
        FileOutputStream fos = new FileOutputStream(destPath);

        // 读取和写入信息
        int len = 0;
        while ((len = fis.read()) != -1) {
            fos.write(len);
        }
        // 关闭流  先开后关  后开先关
        fos.close(); // 后开先关
        fis.close(); // 先开后关
    }

}
