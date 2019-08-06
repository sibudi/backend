/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.configurations;

import com.yqg.common.filters.LogFilter;
import com.yqg.common.filters.RequestBodyLoggingFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// NOTE: Filter registration is NOT necessary, until we need to specify some behaviors like UrlPatterns. Spring boot looks up for
// all beans annotated with @Component of sub-type filter. For example, even if we don't add function registerRequestBodyLoggingFilterBean(),
// RequestBodyLoggingFilter will still be mapped and loaded.
/**
 * @author Jacob
 *
 */
//@Configuration
public class FilterRegister {

//    @Bean
//    public FilterRegistrationBean registerLogFilterBean(LogFilter filter) {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
//        filterRegistrationBean.setOrder(1);
//        filterRegistrationBean.setEnabled(true);
//        filterRegistrationBean.addUrlPatterns("/users/*", "/systems/*", "/order/*", "/system/*", "/upload/*", "/userBaseInfo/*", "/userBank/*", "/sysDist/*", "/index/*", "/operator/*","/repay/*","/web/users/*");
//        return filterRegistrationBean;
//    }
//
//    @Bean
//    public FilterRegistrationBean registerRequestBodyLoggingFilterBean(
//            RequestBodyLoggingFilter filter) {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
//        filterRegistrationBean.setOrder(4);
//        filterRegistrationBean.setEnabled(true);
//        filterRegistrationBean.addUrlPatterns("/users/*", "/systems/*", "/order/*", "/system/*", "/upload/*", "/userBaseInfo/*", "/userBank/*", "/sysDist/*", "/index/*", "/operator/*","/repay/*","/web/users/*");
//        return filterRegistrationBean;
//    }
}
