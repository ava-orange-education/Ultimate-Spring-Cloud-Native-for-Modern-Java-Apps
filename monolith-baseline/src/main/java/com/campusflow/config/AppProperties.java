package com.campusflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "campusflow")
public class AppProperties {

    private String schoolName = "CampusFlow Academy";
    private FeatureFlags features = new FeatureFlags();
    private NotificationSettings notifications = new NotificationSettings();

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public FeatureFlags getFeatures() {
        return features;
    }

    public void setFeatures(FeatureFlags features) {
        this.features = features;
    }

    public NotificationSettings getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationSettings notifications) {
        this.notifications = notifications;
    }

    public static class FeatureFlags {
        private boolean attendanceReminders = true;
        private boolean enrollmentConfirmation = true;

        public boolean isAttendanceReminders() {
            return attendanceReminders;
        }

        public void setAttendanceReminders(boolean attendanceReminders) {
            this.attendanceReminders = attendanceReminders;
        }

        public boolean isEnrollmentConfirmation() {
            return enrollmentConfirmation;
        }

        public void setEnrollmentConfirmation(boolean enrollmentConfirmation) {
            this.enrollmentConfirmation = enrollmentConfirmation;
        }
    }

    public static class NotificationSettings {
        private String fromAddress = "noreply@campusflow.example";

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }
    }
}
