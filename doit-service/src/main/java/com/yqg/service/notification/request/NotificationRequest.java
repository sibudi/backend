package com.yqg.service.notification.request;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class NotificationRequest {
    
    @JsonProperty
    private String to;

    @JsonProperty
    private String message;
    
    @JsonProperty
    private String from;
    
    @JsonProperty
    private String cc;
    
    @JsonProperty
    private String bcc;

    @JsonProperty
    private String subject;
    
    @JsonProperty("webhook_url")
    private String webhookUrl; //Used by SlackNotificationService

    @JsonProperty
    private ArrayList<String> registration_ids; //Used by FCM Notification

    @JsonProperty
    private String action;

    public enum NotificationChannel {
        EMAIL, SLACK, FCM;
    }
    public enum NotificationStatus {
        SUCCESS, FAILED;
    }
}
