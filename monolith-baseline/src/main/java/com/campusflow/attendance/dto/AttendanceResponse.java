package com.campusflow.attendance.dto;

import com.campusflow.attendance.entity.AttendanceRecord;
import com.campusflow.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public record AttendanceResponse(
        Long id,
        Long studentId,
        String studentName,
        Long classId,
        String className,
        LocalDate date,
        AttendanceStatus status
) {

    public static AttendanceResponse from(AttendanceRecord record) {
        return new AttendanceResponse(
                record.getId(),
                record.getStudent().getId(),
                record.getStudent().getFirstName() + " " + record.getStudent().getLastName(),
                record.getSchoolClass().getId(),
                record.getSchoolClass().getName(),
                record.getDate(),
                record.getStatus()
        );
    }
}
