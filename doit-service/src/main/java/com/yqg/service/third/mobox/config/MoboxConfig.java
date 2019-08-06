package com.yqg.service.third.mobox.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 同盾配置
 * Created by wanghuaizhou on 2019/2/25.
 */
@Data
@ConfigurationProperties(prefix = "mobox")
@Configuration
public class MoboxConfig {

    private String partnerCode;

    private String partnerKey;

}
