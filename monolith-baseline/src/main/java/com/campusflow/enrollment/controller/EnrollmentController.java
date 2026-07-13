package com.campusflow.enrollment.controller;

import com.campusflow.enrollment.dto.EnrollmentRequest;
import com.campusflow.enrollment.dto.EnrollmentResponse;
import com.campusflow.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/by-student/{studentId}")
    public List<EnrollmentResponse> byStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @GetMapping("/by-class/{classId}")
    public List<EnrollmentResponse> byClass(@PathVariable Long classId) {
        return enrollmentService.findByClass(classId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enroll(@Valid @RequestBody EnrollmentRequest request) {
        return enrollmentService.enroll(request);
    }
}
