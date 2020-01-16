package com.yqg.service.third.advance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * advance
 * Created by Jacob on 2017/11/26.
 */
@Data
@ConfigurationProperties(prefix = "third.advance")
@Configuration
public class AdvanceConfig {
    /**
     */
    private String accessKey;
    /**
     */
    private String secretKey;
    /**
     */
    private String apiHost;
    /**
     */
    private String identityCheckApi;

    private String blacklistCheck;

    private String multiPlatform;

    private boolean switchOn;

}
