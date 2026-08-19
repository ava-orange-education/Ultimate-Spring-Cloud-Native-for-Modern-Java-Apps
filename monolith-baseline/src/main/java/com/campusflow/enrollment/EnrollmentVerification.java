package com.campusflow.enrollment;

/**
 * Defines the enrollment verification capability as seen by the Attendance domain.
 *
 * <p>Attendance needs to know whether a student is enrolled in a class before recording
 * presence or absence. How that information is obtained is intentionally hidden behind
 * this interface so that the Attendance module does not depend on persistence details
 * of the Enrollment module.
 *
 * <p>The current implementation delegates to a local repository. A future implementation
 * could call a REST endpoint, consume a replicated read model, or subscribe to enrollment
 * events — none of which would require changes to {@code AttendanceService}.
 */
public interface EnrollmentVerification {

    /**
     * Returns {@code true} if the student identified by {@code studentId} is currently
     * enrolled in the class identified by {@code classId}.
     */
    boolean isEnrolled(Long studentId, Long classId);
}
