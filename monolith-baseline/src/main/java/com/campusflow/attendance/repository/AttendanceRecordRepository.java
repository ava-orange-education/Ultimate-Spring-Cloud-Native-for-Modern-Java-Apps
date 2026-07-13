package com.campusflow.attendance.repository;

import com.campusflow.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findBySchoolClassIdAndDate(Long classId, LocalDate date);

    Optional<AttendanceRecord> findByStudentIdAndSchoolClassIdAndDate(
            Long studentId, Long classId, LocalDate date);
}
