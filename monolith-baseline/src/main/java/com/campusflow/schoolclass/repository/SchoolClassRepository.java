package com.campusflow.schoolclass.repository;

import com.campusflow.schoolclass.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
}
