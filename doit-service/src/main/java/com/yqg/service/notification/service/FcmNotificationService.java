package com.yqg.service.notification.service;

import java.io.IOException;

import com.yqg.common.utils.JsonUtils;
import com.yqg.service.notification.config.NotificationConfig;
import com.yqg.service.notification.request.NotificationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Setiya Budi on 2020/02/13.
 */
@Slf4j
@Service
public class FcmNotificationService implements NotificationService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private NotificationConfig notificationConfig;

    @Override
    public String SendNotification(NotificationRequest notificationRequest) {
        try { //RequestBody content harus byte (getBytes), kalau string dia akan kirim "application/json; charset=utf-8" akan menghasilkan error: "Invalid Content Type"
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                    JsonUtils.serialize(notificationRequest).getBytes());
            //log.info(JsonUtils.serialize(notificationRequest));
            
            Request request = new Request.Builder().url(notificationConfig.getNotificatonServiceUrl() + "/push")
                    .header("x-authorization-token", notificationConfig.getNotificatonServiceToken())
                    .header("Origin", notificationConfig.getOrigin())
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            
            //log.info(response.toString());
            return response.body().string();
        } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
            String errorMessage = String.format("Error when mapping Object to Json String: %s", jpe);
            log.error(errorMessage);
            log.info(errorMessage);
            return errorMessage;
        } catch (IOException ioe) {
            String errorMessage = String.format("Exception when sending slack notification: %s", ioe);
            log.error(errorMessage);
            log.info(errorMessage);
            return errorMessage;
        } catch (Exception e) {
            String errorMessage = String.format("General Exception when sending notification: %s", e);
            log.info(errorMessage);
            return errorMessage;
        }
    } 
}
