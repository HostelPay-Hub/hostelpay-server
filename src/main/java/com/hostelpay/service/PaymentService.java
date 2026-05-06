package com.hostelpay.service;

import com.hostelpay.dto.PaymentResponseDTO;
import com.hostelpay.dto.RecordPaymentRequestDTO;
import com.hostelpay.entities.*;
import com.hostelpay.repositories.*;
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
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private HostelRepository hostelRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    private UUID getCurrentHostelId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal p) {
            return p.getHostelId();
        }
        throw new RuntimeException("Unauthorized");
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal p) {
            return p.getEmail();
        }
        return "system";
    }

    public PaymentResponseDTO recordPayment(RecordPaymentRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Hostel hostel = hostelRepository.findById(hostelId).orElseThrow(() -> new RuntimeException("Hostel not found"));
        Student student = studentRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) throw new RuntimeException("Unauthorized");

        Payment payment = new Payment();
        payment.setHostel(hostel);
        payment.setStudent(student);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setReferenceNotes(request.getReferenceNotes());
        payment.setIsActive(true);

        Payment saved = paymentRepository.save(payment);

        AuditLog audit = AuditLog.builder().entityName("Payment").entityId(saved.getId()).action(AuditLog.AuditAction.CREATE).performedBy(getCurrentEmail()).newValue("Amount:" + saved.getAmount()).build();
        auditLogRepository.save(audit);

        log.info("Payment recorded: {} amount: {}", saved.getId(), saved.getAmount());
        return mapToDTO(saved);
    }

    public List<PaymentResponseDTO> getPaymentsByStudent(UUID studentId) {
        UUID hostelId = getCurrentHostelId();
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) throw new RuntimeException("Unauthorized");
        return paymentRepository.findActivePaymentsByStudentId(studentId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findActivePaymentsByHostelId(getCurrentHostelId()).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deletePayment(UUID paymentId) {
        UUID hostelId = getCurrentHostelId();
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        if (!payment.getHostel().getId().equals(hostelId)) throw new RuntimeException("Unauthorized");
        payment.setIsActive(false);
        paymentRepository.save(payment);
        AuditLog audit = AuditLog.builder().entityName("Payment").entityId(paymentId).action(AuditLog.AuditAction.DELETE).performedBy(getCurrentEmail()).oldValue("Amount:" + payment.getAmount()).build();
        auditLogRepository.save(audit);
        log.info("Payment soft-deleted: {}", paymentId);
    }

    private PaymentResponseDTO mapToDTO(Payment p) {
        return PaymentResponseDTO.builder().id(p.getId()).hostelId(p.getHostel().getId()).studentId(p.getStudent().getId()).studentName(p.getStudent().getFullName()).amount(p.getAmount()).paymentDate(p.getPaymentDate()).paymentMethod(p.getPaymentMethod().name()).status(p.getStatus().name()).referenceNotes(p.getReferenceNotes()).isActive(p.getIsActive()).createdAt(p.getCreatedAt()).build();
    }
}
