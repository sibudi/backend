package com.yqg.service.third.asli.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghuaizhou on 2019/3/25.
 */
@Data
@ConfigurationProperties(prefix = "third.asli")
@Configuration
public class AsliConfig {
    /**
     */
    private String name;
    /**
     */
    private String token;
    /**
     */
    private String host;
//    /**
//     *  实名验证url
//     */
//    private String identityUrl;
//    /**
//     *  自拍照验证url
//     */
//    private String selfieUrl;

    private String verifyProfessionalPlus;
}
