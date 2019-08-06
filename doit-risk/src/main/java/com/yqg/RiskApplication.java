/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg;

import com.yqg.common.converter.CustomerMappingJackson2HttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@Slf4j
@ComponentScan(basePackages = "com.yqg",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {CustomerMappingJackson2HttpMessageConverter.class}))
public class RiskApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RiskApplication.class, args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            log.warn("spring boot use profile :{}", profile);
        }

    }


}

