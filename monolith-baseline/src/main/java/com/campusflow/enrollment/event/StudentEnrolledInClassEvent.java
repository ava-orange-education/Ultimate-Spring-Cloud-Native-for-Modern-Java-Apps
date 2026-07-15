package com.campusflow.enrollment.event;

/**
 * Domain event published after a student is successfully enrolled in a class.
 */
public record StudentEnrolledInClassEvent(
        Long enrollmentId,
        Long studentId,
        String studentEmail,
        String studentFirstName,
        Long classId,
        String className,
        String classTerm
) {
}
