package com.yqg.service.third.mobox.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "tongdun")
@Configuration
public class TongDunConfig {

    private String partnerCode;

    private String partnerKey;

    private String appName;

    // 贷前保镖访问url
    private String checkBeforeCreditUrl;
}
