package com.yqg.service.third.twilio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 上午10:14
 */
@Data
@ConfigurationProperties(prefix = "third.twilio")
@Configuration
public class TwilioConfig {

    private String accountSid;

    private String authToken;

    private String from; //系统外呼的电话

    private String demoUrl;

    private String url;

    private String serverAddress; //服务器地址

    private String anOtherServerAddress; //另一台服务器地址

    private String whatsAppFrom;//whatsapp的外显号码

}
