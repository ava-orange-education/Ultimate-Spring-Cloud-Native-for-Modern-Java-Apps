package com.campusflow.notification.listener;

import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import com.campusflow.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnrollmentNotificationListenerTest {

    private NotificationService notificationService;
    private AppProperties appProperties;
    private EnrollmentNotificationListener listener;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        appProperties = new AppProperties();
        listener = new EnrollmentNotificationListener(notificationService, appProperties);
    }

    @Test
    void sendsNotificationWhenFeatureEnabled() {
        appProperties.getFeatures().setEnrollmentConfirmation(true);

        StudentEnrolledInClassEvent event = new StudentEnrolledInClassEvent(
                1L, 2L, "alex@example.com", "Alex", 3L, "Algebra I", "Fall 2026");

        listener.onStudentEnrolledInClass(event);

        verify(notificationService).handleStudentEnrolled(event);
    }

    @Test
    void skipsNotificationWhenFeatureDisabled() {
        appProperties.getFeatures().setEnrollmentConfirmation(false);

        StudentEnrolledInClassEvent event = new StudentEnrolledInClassEvent(
                1L, 2L, "alex@example.com", "Alex", 3L, "Algebra I", "Fall 2026");

        listener.onStudentEnrolledInClass(event);

        verify(notificationService, never()).handleStudentEnrolled(event);
    }
}
