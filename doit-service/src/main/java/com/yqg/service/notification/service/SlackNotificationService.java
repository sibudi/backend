package com.yqg.service.notification.service;

import com.yqg.service.notification.config.NotificationConfig;
import com.yqg.service.notification.request.NotificationRequest;
import com.yqg.common.utils.JsonUtils;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SlackNotificationService implements NotificationService {
    
    @Autowired
    private NotificationConfig notificationConfig;

    @Autowired
    private OkHttpClient okHttpClient;
 
    @Override
    public String SendNotification(NotificationRequest notificationRequest) {
        try {
            notificationRequest.setWebhookUrl(notificationConfig.getWebhookUrl());

            RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), 
                JsonUtils.serialize(notificationRequest));

            Request request = new Request.Builder()
                    .url(notificationConfig.getNotificatonServiceUrl()+"/slack")
                    .header("x-authorization-token", notificationConfig.getNotificatonServiceToken())
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        }
        catch(com.fasterxml.jackson.core.JsonProcessingException jpe){
            String errorMessage = String.format("Error when mapping Object to Json String: %s", jpe);
            log.error(errorMessage);
            return errorMessage;
        }
        catch (IOException ioe) {
            String errorMessage = String.format("Exception when sending slack notification: %s", ioe);
            log.error(errorMessage);
            return errorMessage;
        }
    }

}