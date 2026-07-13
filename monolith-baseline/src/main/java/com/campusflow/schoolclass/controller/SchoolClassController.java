package com.campusflow.schoolclass.controller;

import com.campusflow.schoolclass.dto.SchoolClassRequest;
import com.campusflow.schoolclass.dto.SchoolClassResponse;
import com.campusflow.schoolclass.service.SchoolClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public List<SchoolClassResponse> list() {
        return schoolClassService.findAll();
    }

    @GetMapping("/{id}")
    public SchoolClassResponse get(@PathVariable Long id) {
        return schoolClassService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolClassResponse create(@Valid @RequestBody SchoolClassRequest request) {
        return schoolClassService.create(request);
    }
}
