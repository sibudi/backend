package com.yqg.upload.utils;

import com.yqg.upload.common.Base64Info;
import com.yqg.upload.common.FileStorage;
import com.yqg.upload.config.UploadConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 */
public class ImgStorageHandle implements ImgHandle {

    Logger logger = LoggerFactory.getLogger(ImgStorageHandle.class);

    private String params;

    private MultipartFile file;

    private InputStream is;

    private Base64Info info;

    public ImgStorageHandle(MultipartFile file, String params) {
        this.file = file;
        this.params = params;
        try {
            this.is = file.getInputStream();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public ImgStorageHandle(InputStream is, String params) {
        this.is = is;
        this.params = params;
    }

    public ImgStorageHandle(InputStream is, Base64Info info, String params) {
        this.is = is;
        this.info = info;
        this.params = params;
    }


    /**
     */
    @Override
    public void handle(UploadConfig uploadConfig) {
        ImgHandleResultException img = new ImgHandleResultException();

        if (file == null) {
            img.setInfo(storageBase64(uploadConfig));
            img.setStream(FileUtils.GenerateImage(info.getBase64Str()));
        } else {
            img.setInfo(this.storageFile(uploadConfig));
            try {
                byte[] bytes = IOUtils.toByteArray(new FileInputStream(img.getInfo().getFileUrl()));
                img.setStream(bytes);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        }
        img.setHasStoraged(true);
        throw img;
    }

    /**
     */
    private FileStorage storageFile(UploadConfig uploadConfig) {

        //?????????????
        String originalFilename = file.getOriginalFilename();

        //??fileStorage??
        FileStorage info = new FileStorage();
        info.setFileName(originalFilename);
        info.setFileType(file.getContentType());
        info.setFileSize(String.valueOf(file.getSize()));
        String fileName = FileUtils.getFileName(originalFilename);
        info.setFileUrl(FileUtils.getFilePath(fileName,originalFilename,uploadConfig.getRootPath()));
        if (params == null) params = "";
        info.setParams(params);

        //????????,??????????????????
        if (!originalFilename.trim().equals("") ) {
            String path = info.getFileUrl();
            logger.info("上传A区图片路径为:" + path);
            File localFile = new File(path);

            //???????,????
            if (!localFile.getParentFile().exists() && !localFile.isDirectory()) {
                logger.info("A区文件夹路径为:{}=========", localFile.getParentFile().getPath());
                localFile.getParentFile().mkdirs();
            }
            BufferedOutputStream out=null;
            try {
                out = new BufferedOutputStream(
                        new FileOutputStream(localFile));
                out.write(file.getBytes());
                out.flush();
            } catch (IOException e) {
                logger.error("??:" + originalFilename + "????!" + e);
                return null;
            }
            finally {
                if(out!=null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return info;
    }

    private FileStorage storageBase64(UploadConfig uploadConfig) {
        FileStorage fs = new FileStorage();
        fs.setFileName(info.getFileName());
        fs.setFileType(info.getFileType());
        fs.setFileSize(String.valueOf(info.getBase64Str().length()));
        String fileName = FileUtils.getFileName(info.getFileName());

        fs.setFileUrl(FileUtils.getFilePath(fileName, fs.getFileType(),uploadConfig.getRootPath()));
        if (params == null) params = "";
        fs.setParams(params);

        //????????,??????????????????
        if (info.getFileName().trim() != "") {
            String path = fs.getFileUrl();
            logger.info("上传A区图片路径为:" + path);
            File localFile = new File(path);

            //???????,????
            if (!localFile.getParentFile().exists() && !localFile.getParentFile().isDirectory()) {
                logger.info("A区文件夹路径为:{}=========", localFile.getParentFile().getPath());

                localFile.getParentFile().mkdirs();
            }

            try {
                OutputStream os = new FileOutputStream(localFile);
                int bytesRead = 0;
                byte[] buffer = new byte[1024 * 8];
                while ((bytesRead = is.read(buffer, 0, 1024 * 8)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                is.close();
            } catch (IOException e) {
                logger.error("??:" + fs.getFileName() + "????!" + e);
                return null;
            }
        }
        return fs;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public Base64Info getInfo() {
        return info;
    }

    public void setInfo(Base64Info info) {
        this.info = info;
    }
}
