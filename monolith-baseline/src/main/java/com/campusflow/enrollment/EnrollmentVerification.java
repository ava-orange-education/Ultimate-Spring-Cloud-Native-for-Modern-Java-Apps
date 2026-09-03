package com.campusflow.enrollment;

/**
 * Defines the enrollment verification capability exposed by the Enrollment domain.
 *
 * <p>Consumers (for example Attendance) can ask whether a student is enrolled in a class
 * without depending on Enrollment persistence details. How the answer is obtained stays
 * behind this interface.
 *
 * <p>The current implementation delegates to a local repository. A future implementation
 * could call a REST endpoint, consume a replicated read model, or subscribe to enrollment
 * events — none of which would require changes to callers such as {@code AttendanceService}.
 */
public interface EnrollmentVerification {

    /**
     * Returns {@code true} if the student identified by {@code studentId} is currently
     * enrolled in the class identified by {@code classId}.
     */
    boolean isEnrolled(Long studentId, Long classId);
}
