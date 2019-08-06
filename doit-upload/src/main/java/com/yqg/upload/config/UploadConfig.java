package com.yqg.upload.config;

import lombok.Data;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * ????????
 * Created by Jacob on 2017/11/24.
 */
@ConfigurationProperties(prefix = "upload")
@Configuration
@Data
public class UploadConfig {
    /**
     * ??????
     */
    private String rootPath;

    private String rootPathBak;
    /**
     * ??????
     */
    private String maxFileSize;
    /**
     * ?????
     */
    private String maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // ???????? ,??????????????
        // ???????????????????????;
        factory.setMaxFileSize(this.getMaxFileSize()); // KB,MB
        /// ??????????
        factory.setMaxRequestSize(this.getMaxRequestSize());
        // Sets the directory location where files will be stored.
        // factory.setLocation("????");
        return factory.createMultipartConfig();
    }
}
