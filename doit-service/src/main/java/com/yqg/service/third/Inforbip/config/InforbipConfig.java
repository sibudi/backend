package com.yqg.service.third.Inforbip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghuaizhou on 2018/8/8.
 */
@Data
@ConfigurationProperties(prefix = "third.inforbip")
@Configuration
public class InforbipConfig {


    private String userName;

    private String password;

    private String host;

    private String mobileLookUpUrl;

    private String mobileLookUpUrlMulti;

    private String reportUrl;

    private String fromTel;   // 外呼显示的号码
}
