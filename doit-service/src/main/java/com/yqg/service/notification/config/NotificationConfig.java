package com.yqg.service.notification.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/*****
 * @Author arief.halim
 * Created at 2019/11/25
 * @Email arief.halim@do-it.id
 *
 ****/
@Data
@Configuration
public class NotificationConfig {

    @Value("${notification.service.url}")
    private String notificatonServiceUrl;
    
    @Value("${notification.service.token}")
    private String notificatonServiceToken;

    @Value("${slack.webhook.url}")
    private String webhookUrl;
    
}