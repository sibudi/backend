package com.yqg.service.third.jxl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "third.jxl")
@Component
public class JXLConfig {
    private String apiKey;
    private String secret;
    private String createReportTaskUrl; //创建报表任务url
    private String ktpVerifyUrl; //使命验证url
}
