package com.yqg.service.third.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * advance
 * Created by Jacob on 2017/11/26.
 */
@Data
@ConfigurationProperties(prefix = "third.upload")
@Configuration
public class UploadConfig {
    /**
     */
    private String uploadHost;
    /**
     */
    private String uploadFilePath;
    /**
     */
    private String uploadBase64Path;

    /*
     * 不需要验证的上传接口Post请求
     * */
    private String uploadManageFileUnverified;
}
