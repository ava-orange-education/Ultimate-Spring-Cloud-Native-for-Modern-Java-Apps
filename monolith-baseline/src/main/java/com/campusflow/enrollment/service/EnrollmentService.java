package com.campusflow.enrollment.service;

import com.campusflow.common.exception.BusinessRuleException;
import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.dto.EnrollmentRequest;
import com.campusflow.enrollment.dto.EnrollmentResponse;
import com.campusflow.enrollment.entity.Enrollment;
import com.campusflow.enrollment.repository.EnrollmentRepository;
import com.campusflow.notification.service.NotificationService;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.schoolclass.service.SchoolClassService;
import com.campusflow.student.entity.Student;
import com.campusflow.student.entity.StudentStatus;
import com.campusflow.student.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Enrollment spans student and class domains. It loads entities through sibling
 * services and triggers notifications — a realistic monolith coupling point.
 */
@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final NotificationService notificationService;
    private final AppProperties appProperties;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentService studentService,
            SchoolClassService schoolClassService,
            NotificationService notificationService,
            AppProperties appProperties) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.notificationService = notificationService;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByClass(Long classId) {
        return enrollmentRepository.findBySchoolClassId(classId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    public EnrollmentResponse enroll(EnrollmentRequest request) {
        Student student = studentService.getStudent(request.studentId());
        SchoolClass schoolClass = schoolClassService.getSchoolClass(request.classId());

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessRuleException("Only active students can enroll");
        }
        if (enrollmentRepository.existsByStudentIdAndSchoolClassId(request.studentId(), request.classId())) {
            throw new BusinessRuleException("Student is already enrolled in this class");
        }
        if (enrollmentRepository.countActiveByClassId(request.classId()) >= schoolClass.getCapacity()) {
            throw new BusinessRuleException("Class is at capacity");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSchoolClass(schoolClass);
        Enrollment saved = enrollmentRepository.save(enrollment);

        if (appProperties.getFeatures().isEnrollmentConfirmation()) {
            notificationService.sendEnrollmentConfirmation(student, schoolClass);
        }

        return EnrollmentResponse.from(saved);
    }
}
