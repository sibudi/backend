package com.yqg.config;

import com.yqg.interceptor.AuthorizationInterceptor;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/


@Configuration
public class ManagementWebAppConfig extends WebMvcConfigurerAdapter {

    private String[] swagger2Urls = {"/swagger*","/v2/api-docs","/configuration/ui","/configuration/security"};

    @Bean
    AuthorizationInterceptor localInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/manage/sysLogin").excludePathPatterns("/manage/forceChangePassword.json")
//                .excludePathPatterns("/manage/twilioD_1XML")
//                .excludePathPatterns("/manage/twilioD_2XML")
//                .excludePathPatterns("/manage/twilioD_3XML")
//                .excludePathPatterns("/manage/twilioD0XML")
                .excludePathPatterns("/manage/getXmlFile")
                .excludePathPatterns("/manage/getTwilioWaResponses")
                .excludePathPatterns("/manage/twilioWhatsAppResXML")
//                .excludePathPatterns("/manage/silentUserXML")
//                .excludePathPatterns("/manage/upgradeLimitXML")
//                .excludePathPatterns("/manage/reduceLimitXML")
                .excludePathPatterns("/manage/twilioMP3")
                .excludePathPatterns("/manage/getExcelFile")
                .excludePathPatterns("/manage/showStreamOnBrowser")
                .excludePathPatterns("/**/managerTask")
                .excludePathPatterns("/**/twilioXml")
            .excludePathPatterns(swagger2Urls);
    }
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("10240KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("102400KB");
        return factory.createMultipartConfig();
    }
}
