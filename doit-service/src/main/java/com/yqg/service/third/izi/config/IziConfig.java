package com.yqg.service.third.izi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wanghuaizhou on 2018/12/5.
 */
@Data
@ConfigurationProperties(prefix = "third.izi")
@Configuration
public class IziConfig {
    /**
     * 公钥key
     */
    private String accessKey;
    /**
     * 秘钥key
     */
    private String secretKey;

    /**
     * 手机号码实名认证请求地址
     */
    private String phoneVerifyUrl;

    /**
     * 手机在网时长请求地址
     */
    private String phoneAgeUrl;

    /**
     * 身份认证详情v3
     */
    private String identityCheck3Url;


    /**
     * 检测WhatsApp是否已经开通
     */
    private String monitorWhatsAppUrl;

    //whatsapp详情
    private String whatsAppDetailUrl;
}
