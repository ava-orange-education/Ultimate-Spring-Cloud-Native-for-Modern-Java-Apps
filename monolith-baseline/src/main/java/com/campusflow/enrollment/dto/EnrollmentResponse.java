package com.campusflow.enrollment.dto;

import com.campusflow.enrollment.entity.Enrollment;
import com.campusflow.enrollment.entity.EnrollmentStatus;

import java.time.Instant;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long classId,
        String className,
        EnrollmentStatus status,
        Instant enrolledAt
) {

    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName(),
                enrollment.getSchoolClass().getId(),
                enrollment.getSchoolClass().getName(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt()
        );
    }
}
