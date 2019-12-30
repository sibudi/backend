package com.yqg.common.configurations;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 07/02/2018
 * Time: 1:45 PM
 */
@Configuration
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();
                // .connectTimeout(30, TimeUnit.SECONDS)
                // .writeTimeout(30, TimeUnit.SECONDS)
                // .readTimeout(60, TimeUnit.SECONDS)
                // .build();
    }
}
