package com.campusflow.enrollment.service;

import com.campusflow.enrollment.dto.EnrollmentRequest;
import com.campusflow.enrollment.entity.Enrollment;
import com.campusflow.enrollment.event.StudentEnrolledInClassEvent;
import com.campusflow.enrollment.repository.EnrollmentRepository;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.schoolclass.service.SchoolClassService;
import com.campusflow.student.entity.Student;
import com.campusflow.student.entity.StudentStatus;
import com.campusflow.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnrollmentServiceTest {

    private EnrollmentRepository enrollmentRepository;
    private StudentService studentService;
    private SchoolClassService schoolClassService;
    private ApplicationEventPublisher eventPublisher;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        studentService = mock(StudentService.class);
        schoolClassService = mock(SchoolClassService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        enrollmentService = new EnrollmentService(
                enrollmentRepository, studentService, schoolClassService, eventPublisher);
    }

    @Test
    void enrollPublishesStudentEnrolledInClassEvent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Alex");
        student.setEmail("alex@student.campusflow.example");
        student.setStatus(StudentStatus.ACTIVE);

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(2L);
        schoolClass.setName("World History");
        schoolClass.setTerm("Fall 2026");
        schoolClass.setCapacity(30);

        when(studentService.getStudent(1L)).thenReturn(student);
        when(schoolClassService.getSchoolClass(2L)).thenReturn(schoolClass);
        when(enrollmentRepository.existsByStudentIdAndSchoolClassId(1L, 2L)).thenReturn(false);
        when(enrollmentRepository.countActiveByClassId(2L)).thenReturn(5L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            enrollment.setId(99L);
            return enrollment;
        });

        enrollmentService.enroll(new EnrollmentRequest(1L, 2L));

        var captor = org.mockito.ArgumentCaptor.forClass(StudentEnrolledInClassEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        StudentEnrolledInClassEvent event = captor.getValue();
        assertThat(event.enrollmentId()).isEqualTo(99L);
        assertThat(event.studentId()).isEqualTo(1L);
        assertThat(event.studentEmail()).isEqualTo("alex@student.campusflow.example");
        assertThat(event.className()).isEqualTo("World History");
    }
}
