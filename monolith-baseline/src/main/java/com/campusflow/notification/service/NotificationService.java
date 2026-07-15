package com.campusflow.notification.service;

import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import com.campusflow.notification.entity.Notification;
import com.campusflow.notification.entity.NotificationStatus;
import com.campusflow.notification.entity.NotificationType;
import com.campusflow.notification.repository.NotificationRepository;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.student.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Sends and stores notifications. Enrollment confirmations are triggered by domain events;
 * absence alerts are still called directly from attendance.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;

    public NotificationService(NotificationRepository notificationRepository, AppProperties appProperties) {
        this.notificationRepository = notificationRepository;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public void handleStudentEnrolled(StudentEnrolledInClassEvent event) {
        String subject = "Enrollment confirmed: " + event.className();
        String body = "Hello " + event.studentFirstName() + ", you are enrolled in "
                + event.className() + " for " + event.classTerm() + ".";
        dispatch(event.studentEmail(), subject, body, NotificationType.ENROLLMENT_CONFIRMATION);
    }

    public void sendAbsenceAlert(Student student, SchoolClass schoolClass, LocalDate date) {
        String subject = "Absence recorded for " + schoolClass.getName();
        String body = student.getFirstName() + " was marked absent on " + date
                + " in " + schoolClass.getName() + ".";
        dispatch(student.getEmail(), subject, body, NotificationType.ABSENCE_ALERT);
    }

    private void dispatch(String recipient, String subject, String body, NotificationType type) {
        Notification notification = new Notification();
        notification.setRecipientEmail(recipient);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setType(type);
        notification.setStatus(NotificationStatus.SENT);

        notificationRepository.save(notification);

        // In production this would call an external email provider.
        log.info("notification_sent type={} from={} to={} subject={}",
                type, appProperties.getNotifications().getFromAddress(), recipient, subject);
    }
}
