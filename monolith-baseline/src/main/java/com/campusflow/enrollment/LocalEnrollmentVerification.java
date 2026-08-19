package com.campusflow.enrollment;

import com.campusflow.enrollment.repository.EnrollmentRepository;
import org.springframework.stereotype.Component;

/**
 * Local adapter that satisfies {@link EnrollmentVerification} using the
 * {@link EnrollmentRepository} within the same application.
 *
 * <p>This component exists to contain the direct repository dependency so that
 * {@code AttendanceService} no longer crosses a module boundary at the persistence level.
 * The business behaviour is unchanged; only the dependency direction is clarified.
 */
@Component
class LocalEnrollmentVerification implements EnrollmentVerification {

    private final EnrollmentRepository enrollmentRepository;

    LocalEnrollmentVerification(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public boolean isEnrolled(Long studentId, Long classId) {
        return enrollmentRepository.existsByStudentIdAndSchoolClassId(studentId, classId);
    }
}
