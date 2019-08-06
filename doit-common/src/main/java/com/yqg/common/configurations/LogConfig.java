/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.configurations;

import io.jmnarloch.spring.request.correlation.api.RequestCorrelationInterceptor;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jacob
 *
 */
@Configuration
public class LogConfig {

    public static final String CORRELATION_ID = "X-Request-Id";

    //    @Bean
    //    public CommonsRequestLoggingFilter requestLoggingFilter() {
    //        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
    //        crlf.setIncludeClientInfo(true);
    //        crlf.setIncludeQueryString(true);
    //        crlf.setIncludePayload(true);
    //        crlf.setMaxPayloadLength(5120);
    //        return crlf;
    //    }

    @Bean
    public RequestCorrelationInterceptor correlationLoggingInterceptor() {
        return new RequestCorrelationInterceptor() {
            @Override
            public void afterCorrelationIdSet(String correlationId) {
                MDC.put(CORRELATION_ID, correlationId);
            }

            @Override
            public void cleanUp(String arg0) {
            }
        };
    }

}
