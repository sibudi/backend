package com.yqg.common.configurations;

import com.yqg.common.interceptor.RequestIdGeneratorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*****
 * @Author Jeremy Lawrence
 * Created at 2018/1/30
 *
 *
 ****/

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public RequestIdGeneratorInterceptor requestIdGenerator() {
        return new RequestIdGeneratorInterceptor();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdGenerator());
    }
}
