package com.hostelpay.controller;

import com.hostelpay.dto.CreateStudentRequestDTO;
import com.hostelpay.dto.StudentResponseDTO;
import com.hostelpay.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@Slf4j
public class StudentController {

    @Autowired private StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDTO> create(@Valid @RequestBody CreateStudentRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(req));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CreateStudentRequestDTO req) {
        return ResponseEntity.ok(studentService.updateStudent(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted");
    }
}
