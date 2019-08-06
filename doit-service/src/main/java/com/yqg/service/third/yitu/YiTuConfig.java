package com.yqg.service.third.yitu;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Configuration
@Data
public class YiTuConfig {


    @Value("${third.yitu.url.verify}")
    private String verifyUrl;
    @Value("${third.yitu.url.token}")
    private String token;

}
