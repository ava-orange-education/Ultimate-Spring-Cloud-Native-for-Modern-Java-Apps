package com.campusflow.notification.listener;

import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import com.campusflow.notification.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to enrollment domain events by sending confirmation notifications.
 */
@Component
public class EnrollmentNotificationListener {

    private final NotificationService notificationService;
    private final AppProperties appProperties;

    public EnrollmentNotificationListener(NotificationService notificationService, AppProperties appProperties) {
        this.notificationService = notificationService;
        this.appProperties = appProperties;
    }

    @EventListener
    public void onStudentEnrolledInClass(StudentEnrolledInClassEvent event) {
        if (appProperties.getFeatures().isEnrollmentConfirmation()) {
            notificationService.handleStudentEnrolled(event);
        }
    }
}
