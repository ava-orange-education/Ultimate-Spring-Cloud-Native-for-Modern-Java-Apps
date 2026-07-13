package com.campusflow.student.dto;

import com.campusflow.student.entity.Student;
import com.campusflow.student.entity.StudentStatus;

import java.time.Instant;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        StudentStatus status,
        Instant createdAt
) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getStatus(),
                student.getCreatedAt()
        );
    }
}
