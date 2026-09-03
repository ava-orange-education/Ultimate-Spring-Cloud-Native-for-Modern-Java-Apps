package com.campusflow.attendance.service;

import com.campusflow.attendance.dto.AttendanceRequest;
import com.campusflow.attendance.dto.AttendanceResponse;
import com.campusflow.attendance.entity.AttendanceRecord;
import com.campusflow.attendance.entity.AttendanceStatus;
import com.campusflow.attendance.repository.AttendanceRecordRepository;
import com.campusflow.common.exception.BusinessRuleException;
import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.EnrollmentVerification;
import com.campusflow.notification.service.NotificationService;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.schoolclass.service.SchoolClassService;
import com.campusflow.student.entity.Student;
import com.campusflow.student.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Records attendance and sends absence alerts.
 * Validates that the student is enrolled in the class before recording.
 */
@Service
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EnrollmentVerification enrollmentVerification;
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final NotificationService notificationService;
    private final AppProperties appProperties;

    public AttendanceService(
            AttendanceRecordRepository attendanceRecordRepository,
            EnrollmentVerification enrollmentVerification,
            StudentService studentService,
            SchoolClassService schoolClassService,
            NotificationService notificationService,
            AppProperties appProperties) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.enrollmentVerification = enrollmentVerification;
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.notificationService = notificationService;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> findByClassAndDate(Long classId, LocalDate date) {
        return attendanceRecordRepository.findBySchoolClassIdAndDate(classId, date).stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentService.getStudent(request.studentId());
        SchoolClass schoolClass = schoolClassService.getSchoolClass(request.classId());

        if (!enrollmentVerification.isEnrolled(request.studentId(), request.classId())) {
            throw new BusinessRuleException("Student is not enrolled in this class");
        }

        AttendanceRecord record = attendanceRecordRepository
                .findByStudentIdAndSchoolClassIdAndDate(request.studentId(), request.classId(), request.date())
                .orElseGet(AttendanceRecord::new);

        record.setStudent(student);
        record.setSchoolClass(schoolClass);
        record.setDate(request.date());
        record.setStatus(request.status());

        AttendanceRecord saved = attendanceRecordRepository.save(record);

        if (appProperties.getFeatures().isAttendanceReminders()
                && request.status() == AttendanceStatus.ABSENT) {
            notificationService.sendAbsenceAlert(student, schoolClass, request.date());
        }

        return AttendanceResponse.from(saved);
    }
}
