package com.yqg.service.notification.service;

import org.springframework.stereotype.Service;
import com.yqg.service.notification.request.NotificationRequest;

@Service
public interface NotificationService {
   
    String SendNotification(NotificationRequest notificationRequest);
}