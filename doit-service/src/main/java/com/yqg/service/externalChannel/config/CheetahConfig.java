package com.yqg.service.externalChannel.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by wanghuaizhou on 2018/12/26.
 */
@Component
@Data
public class CheetahConfig {

    @Value("${third.cheetah.accessKey}")
    private String accessKey = "27oovmz7gueaud8l";//aes加密秘钥

    @Value("${third.cheetah.secretKey}")
    private String secretKey = "8b125cc228adcc57"; //sk

    @Value("${third.cashcash.partnerName}")
    private String partnerName="Do-It";

}
