/**
 * Notification domain — composes and delivers enrollment confirmations and absence alerts.
 * <p>
 * Role in the monolith: side-effect handler that reacts to events from other domains.
 * Listens for {@link com.campusflow.enrollment.event.StudentEnrolledInClassEvent}
 * via {@link com.campusflow.notification.listener.EnrollmentNotificationListener}.
 * Owns the {@code notifications} table. Strong first extraction candidate.
 */
package com.campusflow.notification;
