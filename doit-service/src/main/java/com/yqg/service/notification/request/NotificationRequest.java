package com.yqg.service.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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
    
    public enum NotificationChannel {
        EMAIL, SLACK;
    }
    public enum NotificationStatus {
        SUCCESS, FAILED;
    }
}
