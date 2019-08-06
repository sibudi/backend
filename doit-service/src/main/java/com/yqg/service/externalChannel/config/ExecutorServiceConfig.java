package com.yqg.service.externalChannel.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/16
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService fixedExecutorService() {
        ExecutorService service = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        return service;
    }

}
