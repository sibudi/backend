package com.yqg.service.third.ojk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghuaizhou on 2019/5/31.
 */
@Data
@ConfigurationProperties(prefix = "third.ojk")
@Configuration
public class OJKConfig {

    private String userName;

    private String password;

    private String uploadUrl;

    private String aesKey;  //AES私钥

    private String iv;  //AES偏移向量
}
