package com.yqg.service.third.upload;

import com.alibaba.fastjson.JSON;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.RSAUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.upload.config.UploadConfig;
import com.yqg.service.third.upload.response.UploadResultInfo;
import lombok.extern.log4j.Log4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Created by Jacob on 2017/11/29.
 */
@Component
@Log4j
public class UploadService {

    @Autowired
    private UploadConfig uploadConfig;

    /**
     * ????????
     *
     * @param sessionId
     * @param filePath
     * @param fileName  指定文件名
     * @return
     */
    public Optional<UploadResultInfo> downloadFileAndUpload(String filePath, String fileName) {
        URL url = null;
        try {
            url = new URL(filePath);
        } catch (MalformedURLException e) {
            log.error("下载路径有问题", e);
            return Optional.empty();
        }
        HttpURLConnection conn = null;
        String serverFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        if (StringUtils.isNotBlank(fileName)) {
            serverFileName = fileName;
        }
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            log.error("下载路径有问题", e);
            return Optional.empty();
        }
        conn.setConnectTimeout(3 * 1000);
        try {
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", new InputStreamBody(conn.getInputStream(), serverFileName)).build();
            UploadResultInfo result = this.upload(uploadConfig.getUploadManageFileUnverified(), reqEntity);
            return Optional.of(result);
        } catch (IOException e) {
            log.error("获取文件流失败", e);
            return Optional.empty();
        }
    }


    /**
     * ????????
     *
     * @param sessionId
     * @param filePath
     * @return
     */
    public UploadResultInfo uploadFile(String sessionId, String filePath) {
        // ?????????FileBody
        return uploadFile(sessionId, new File(filePath));
    }

    public UploadResultInfo uploadFile(String sessionId, InputStream ins, String fileName) {
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", new InputStreamBody(ins, fileName)).addPart("sessionId", this.getStringFileBody(sessionId)).build();
        return this.upload(uploadConfig.getUploadFilePath(), reqEntity);
    }

    public UploadResultInfo uploadFile(String sessionId, File file) {
        // ?????????FileBody
        FileBody fileBody = new FileBody(file);
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", fileBody).addPart("sessionId", this.getStringFileBody(sessionId)).build();
        return this.upload(uploadConfig.getUploadFilePath(), reqEntity);
    }

    /**
     * ??base64??
     *
     * @param sessionId
     * @param base64Str
     * @param fileName
     * @param fileType
     * @return
     */
    public UploadResultInfo uploadBase64Img(String sessionId, String base64Str, String fileName, String fileType) {

        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("fileName", this.getStringFileBody(fileName)).addPart("fileData", this.getStringFileBody(base64Str)).addPart("fileType", this.getStringFileBody(fileType)).addPart("sessionId", this.getStringFileBody(sessionId)).build();
        return this.upload(uploadConfig.getUploadBase64Path(), reqEntity);
    }

    private StringBody getStringFileBody(String content) {
        return new StringBody(content, ContentType.create("text/plain", Consts.UTF_8));
    }

    private UploadResultInfo upload(String urlPath, HttpEntity reqEntity) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        UploadResultInfo uploadResultInfo = null;
        try {
            httpClient = HttpClients.createDefault();

            // ??????????????????? ???servlet
            HttpPost httpPost = new HttpPost(uploadConfig.getUploadHost() + urlPath);
            httpPost.setEntity(reqEntity);

            // ???? ????????
            response = httpClient.execute(httpPost);

            // ??????
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String resultString = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
                byte[] data = Base64Utils.decode(resultString.replaceAll("\"", ""));
                byte[] decodedData = RSAUtils.decryptByPrivateKey(data, RSAUtils.privateKeyStr);
                resultString = new String(decodedData);
                // ??????
                log.info("upload file response : " + resultString);
                uploadResultInfo = JSON.parseObject(resultString, UploadResultInfo.class);
            }
            // ??
            EntityUtils.consume(resEntity);

        } catch (Exception e) {
            log.error("upload file error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("close resource error", e);
            }
        }
        return uploadResultInfo;
    }
}
