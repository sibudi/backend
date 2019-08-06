package com.yqg.service.third.digSign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "third.digiSign")
@Configuration
public class DigiSignConfig {
    private String token;
    private String doitAdminEmail;
    private String registerUrl;// 注册地址
    private String activationUrl;//激活地址
    private String sendDocumentUrl;
    private String signUrl;//签约地址
    private String automaticSignKUser; //公司自动签约kuser
    private String automaticSignEmail; //公司自动签email
    private String automaticSignRealName;//公司自动签姓名

    private String apiUserStatusUrl;
    private String apiDocumentStautsUrl;
    private  String apiDocumentDownloadUrl;

    private String contractDir;//协议文件目录


}
