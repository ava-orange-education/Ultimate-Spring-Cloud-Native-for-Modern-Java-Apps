package com.campusflow.schoolclass.service;

import com.campusflow.common.exception.ResourceNotFoundException;
import com.campusflow.schoolclass.dto.SchoolClassRequest;
import com.campusflow.schoolclass.dto.SchoolClassResponse;
import com.campusflow.schoolclass.entity.SchoolClass;
import com.campusflow.schoolclass.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClassService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> findAll() {
        return schoolClassRepository.findAll().stream().map(SchoolClassResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SchoolClassResponse findById(Long id) {
        return SchoolClassResponse.from(getSchoolClass(id));
    }

    public SchoolClassResponse create(SchoolClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(request.name());
        schoolClass.setTerm(request.term());
        schoolClass.setRoom(request.room());
        schoolClass.setTeacherName(request.teacherName());
        schoolClass.setCapacity(request.capacity());
        return SchoolClassResponse.from(schoolClassRepository.save(schoolClass));
    }

    @Transactional(readOnly = true)
    public SchoolClass getSchoolClass(Long id) {
        return schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }
}
