package com.campusflow.student.service;

import com.campusflow.common.exception.BusinessRuleException;
import com.campusflow.common.exception.ResourceNotFoundException;
import com.campusflow.student.dto.StudentRequest;
import com.campusflow.student.dto.StudentResponse;
import com.campusflow.student.entity.Student;
import com.campusflow.student.entity.StudentStatus;
import com.campusflow.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream().map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return StudentResponse.from(getStudent(id));
    }

    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Student email already exists: " + request.email());
        }
        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setStatus(request.status() != null ? request.status() : StudentStatus.ACTIVE);
        return StudentResponse.from(studentRepository.save(student));
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getStudent(id);
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        if (request.status() != null) {
            student.setStatus(request.status());
        }
        return StudentResponse.from(student);
    }

    @Transactional(readOnly = true)
    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }
}
