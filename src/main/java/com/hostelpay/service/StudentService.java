package com.hostelpay.service;

import com.hostelpay.dto.CreateStudentRequestDTO;
import com.hostelpay.dto.StudentResponseDTO;
import com.hostelpay.entities.BaseEntity;
import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.LeaseContract;
import com.hostelpay.entities.Student;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.repositories.StudentRepository;
import com.hostelpay.security.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HostelRepository hostelRepository;

    private UUID getCurrentHostelId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal) {
            return ((JwtPrincipal) auth.getPrincipal()).getHostelId();
        }
        throw new RuntimeException("Unauthorized: hostelId not found in token");
    }

    public StudentResponseDTO createStudent(CreateStudentRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));

        Student student = new Student();
        student.setHostel(hostel);
        student.setFullName(request.getFullName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setDob(request.getDob());
        student.setAadharUrl(request.getAadharUrl());
        student.setIsActive(true);

        Student saved = studentRepository.save(student);
        log.info("Student created: {} in hostel: {}", saved.getId(), hostelId);
        return mapToDTO(saved);
    }

    public List<StudentResponseDTO> getAllStudents() {
        UUID hostelId = getCurrentHostelId();
        return studentRepository.findActiveStudentsByHostelId(hostelId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public StudentResponseDTO getStudentById(UUID studentId) {
        UUID hostelId = getCurrentHostelId();
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Student does not belong to your hostel");
        }
        return mapToDTO(student);
    }

    public StudentResponseDTO updateStudent(UUID studentId, CreateStudentRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Student does not belong to your hostel");
        }

        student.setFullName(request.getFullName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setDob(request.getDob());
        student.setAadharUrl(request.getAadharUrl());

        Student updated = studentRepository.save(student);
        log.info("Student updated: {} in hostel: {}", studentId, hostelId);
        return mapToDTO(updated);
    }

    public void deleteStudent(UUID studentId) {
        UUID hostelId = getCurrentHostelId();
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Student does not belong to your hostel");
        }
        student.setIsActive(false);
        studentRepository.save(student);
        log.info("Student soft-deleted: {} in hostel: {}", studentId, hostelId);
    }

    private StudentResponseDTO mapToDTO(Student student) {
        return StudentResponseDTO.builder()
            .id(student.getId())
            .hostelId(student.getHostel().getId())
            .fullName(student.getFullName())
            .phoneNumber(student.getPhoneNumber())
            .dob(student.getDob())
            .aadharUrl(student.getAadharUrl())
            .isActive(student.getIsActive())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }
    public com.hostelpay.dto.StudentDashboardDTO getStudentDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = ((JwtPrincipal) auth.getPrincipal()).getUserId();
        
        Student student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Student profile not found"));

        LeaseContract activeLease = student.getLeaseContracts().stream()
            .filter(BaseEntity::getIsActive)
            .findFirst()
            .orElse(null);

        java.math.BigDecimal pending = java.math.BigDecimal.ZERO; // Logic for calculating dues goes here later

        return com.hostelpay.dto.StudentDashboardDTO.builder()
            .studentName(student.getFullName())
            .hostelName(student.getHostel().getName())
            .roomNumber(activeLease != null ? activeLease.getRoom().getRoomNumber() : "Unassigned")
            .monthlyRent(activeLease != null ? activeLease.getAgreedMonthlyRent() : java.math.BigDecimal.ZERO)
            .pendingBalance(pending)
            .status(pending.compareTo(java.math.BigDecimal.ZERO) > 0 ? "Overdue" : "All Clear")
            .roommates(new java.util.ArrayList<>()) // Logic to find roommates in the same room
            .build();
    }
}
