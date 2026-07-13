package com.campusflow.schoolclass.dto;

import com.campusflow.schoolclass.entity.SchoolClass;

import java.time.Instant;

public record SchoolClassResponse(
        Long id,
        String name,
        String term,
        String room,
        String teacherName,
        int capacity,
        Instant createdAt
) {

    public static SchoolClassResponse from(SchoolClass schoolClass) {
        return new SchoolClassResponse(
                schoolClass.getId(),
                schoolClass.getName(),
                schoolClass.getTerm(),
                schoolClass.getRoom(),
                schoolClass.getTeacherName(),
                schoolClass.getCapacity(),
                schoolClass.getCreatedAt()
        );
    }
}
