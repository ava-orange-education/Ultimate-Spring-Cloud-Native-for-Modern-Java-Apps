package com.campusflow.attendance;

import com.campusflow.attendance.dto.AttendanceRequest;
import com.campusflow.attendance.entity.AttendanceRecord;
import com.campusflow.attendance.entity.AttendanceStatus;
import com.campusflow.attendance.repository.AttendanceRecordRepository;
import com.campusflow.attendance.service.AttendanceService;
import com.campusflow.common.exception.BusinessRuleException;
import com.campusflow.config.AppProperties;
import com.campusflow.enrollment.EnrollmentVerification;
import com.campusflow.notification.service.NotificationService;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.schoolclass.service.SchoolClassService;
import com.campusflow.student.entity.Student;
import com.campusflow.student.entity.StudentStatus;
import com.campusflow.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttendanceServiceTest {

    private AttendanceRecordRepository attendanceRecordRepository;
    private EnrollmentVerification enrollmentVerification;
    private StudentService studentService;
    private SchoolClassService schoolClassService;
    private NotificationService notificationService;
    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        attendanceRecordRepository = mock(AttendanceRecordRepository.class);
        enrollmentVerification = mock(EnrollmentVerification.class);
        studentService = mock(StudentService.class);
        schoolClassService = mock(SchoolClassService.class);
        notificationService = mock(NotificationService.class);

        AppProperties appProperties = new AppProperties();
        appProperties.getFeatures().setAttendanceReminders(true);

        attendanceService = new AttendanceService(
                attendanceRecordRepository,
                enrollmentVerification,
                studentService,
                schoolClassService,
                notificationService,
                appProperties);
    }

    @Test
    void rejectsAttendanceWhenStudentIsNotEnrolled() {
        when(studentService.getStudent(1L)).thenReturn(student(1L));
        when(schoolClassService.getSchoolClass(2L)).thenReturn(schoolClass(2L));
        when(enrollmentVerification.isEnrolled(1L, 2L)).thenReturn(false);

        AttendanceRequest request = new AttendanceRequest(1L, 2L, LocalDate.now(), AttendanceStatus.PRESENT);

        assertThatThrownBy(() -> attendanceService.markAttendance(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not enrolled");
    }

    @Test
    void recordsAttendanceWhenStudentIsEnrolled() {
        when(studentService.getStudent(1L)).thenReturn(student(1L));
        when(schoolClassService.getSchoolClass(2L)).thenReturn(schoolClass(2L));
        when(enrollmentVerification.isEnrolled(1L, 2L)).thenReturn(true);
        when(attendanceRecordRepository.findByStudentIdAndSchoolClassIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AttendanceRequest request = new AttendanceRequest(1L, 2L, LocalDate.now(), AttendanceStatus.PRESENT);
        attendanceService.markAttendance(request);

        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    @Test
    void sendsAbsenceAlertWhenStudentIsMarkedAbsent() {
        Student student = student(1L);
        SchoolClass schoolClass = schoolClass(2L);
        LocalDate date = LocalDate.now();

        when(studentService.getStudent(1L)).thenReturn(student);
        when(schoolClassService.getSchoolClass(2L)).thenReturn(schoolClass);
        when(enrollmentVerification.isEnrolled(1L, 2L)).thenReturn(true);
        when(attendanceRecordRepository.findByStudentIdAndSchoolClassIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AttendanceRequest request = new AttendanceRequest(1L, 2L, date, AttendanceStatus.ABSENT);
        attendanceService.markAttendance(request);

        verify(notificationService).sendAbsenceAlert(student, schoolClass, date);
    }

    @Test
    void doesNotSendAlertWhenStudentIsPresent() {
        when(studentService.getStudent(1L)).thenReturn(student(1L));
        when(schoolClassService.getSchoolClass(2L)).thenReturn(schoolClass(2L));
        when(enrollmentVerification.isEnrolled(1L, 2L)).thenReturn(true);
        when(attendanceRecordRepository.findByStudentIdAndSchoolClassIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AttendanceRequest request = new AttendanceRequest(1L, 2L, LocalDate.now(), AttendanceStatus.PRESENT);
        attendanceService.markAttendance(request);

        verify(notificationService, never()).sendAbsenceAlert(any(), any(), any());
    }

    // --- helpers ---

    private Student student(Long id) {
        Student s = new Student();
        s.setId(id);
        s.setFirstName("Alex");
        s.setEmail("alex@campusflow.example");
        s.setStatus(StudentStatus.ACTIVE);
        return s;
    }

    private SchoolClass schoolClass(Long id) {
        SchoolClass c = new SchoolClass();
        c.setId(id);
        c.setName("Algebra I");
        c.setTerm("2026-Spring");
        c.setCapacity(30);
        return c;
    }
}
