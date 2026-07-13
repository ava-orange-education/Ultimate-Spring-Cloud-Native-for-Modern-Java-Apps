package com.campusflow.schoolclass.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SchoolClassRequest(
        @NotBlank String name,
        @NotBlank String term,
        String room,
        @NotBlank String teacherName,
        @Min(1) int capacity
) {
}
