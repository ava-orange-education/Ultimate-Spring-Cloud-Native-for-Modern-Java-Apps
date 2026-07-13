package com.campusflow.notification.dto;

import com.campusflow.notification.entity.Notification;
import com.campusflow.notification.entity.NotificationStatus;
import com.campusflow.notification.entity.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String recipientEmail,
        String subject,
        String body,
        NotificationType type,
        NotificationStatus status,
        Instant createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientEmail(),
                notification.getSubject(),
                notification.getBody(),
                notification.getType(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
