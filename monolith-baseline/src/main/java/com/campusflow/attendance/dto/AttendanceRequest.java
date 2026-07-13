package com.campusflow.attendance.dto;

import com.campusflow.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AttendanceRequest(
        @NotNull Long studentId,
        @NotNull Long classId,
        @NotNull LocalDate date,
        @NotNull AttendanceStatus status
) {
}
