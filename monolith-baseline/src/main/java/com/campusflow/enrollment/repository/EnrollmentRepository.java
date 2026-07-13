package com.campusflow.enrollment.repository;

import com.campusflow.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findBySchoolClassId(Long classId);

    @Query("select count(e) from Enrollment e where e.schoolClass.id = :classId and e.status = 'ACTIVE'")
    long countActiveByClassId(@Param("classId") Long classId);

    boolean existsByStudentIdAndSchoolClassId(Long studentId, Long classId);
}
