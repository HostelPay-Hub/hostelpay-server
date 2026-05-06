package com.hostelpay.service;

import com.hostelpay.dto.CreateLeaseRequestDTO;
import com.hostelpay.dto.LeaseResponseDTO;
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
public class LeaseContractService {

    @Autowired
    private LeaseContractRepository leaseContractRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    private UUID getCurrentHostelId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal) {
            return ((JwtPrincipal) auth.getPrincipal()).getHostelId();
        }
        throw new RuntimeException("Unauthorized: hostelId not found in token");
    }

    public LeaseResponseDTO createLease(CreateLeaseRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));

        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Student does not belong to your hostel");
        }

        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Room does not belong to your hostel");
        }

        LeaseContract lease = new LeaseContract();
        lease.setHostel(hostel);
        lease.setStudent(student);
        lease.setRoom(room);
        lease.setStartDate(request.getStartDate());
        lease.setEndDate(request.getEndDate());
        lease.setAgreedMonthlyRent(request.getAgreedMonthlyRent());
        lease.setBillingAnchorDate(request.getBillingAnchorDate());
        lease.setIsActive(true);

        if (request.getPaymentTerm() != null) {
            lease.setPaymentTerm(LeaseContract.PaymentTerm.valueOf(request.getPaymentTerm()));
        }

        LeaseContract saved = leaseContractRepository.save(lease);
        log.info("Lease created: {} student: {} room: {}", saved.getId(), request.getStudentId(), request.getRoomId());
        return mapToDTO(saved);
    }

    public List<LeaseResponseDTO> getAllLeases() {
        UUID hostelId = getCurrentHostelId();
        return leaseContractRepository.findActiveLeasesByHostelId(hostelId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<LeaseResponseDTO> getLeasesByStudent(UUID studentId) {
        UUID hostelId = getCurrentHostelId();
        return leaseContractRepository.findActiveLeasesByStudentId(studentId)
            .stream()
            .filter(lc -> lc.getHostel().getId().equals(hostelId))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteLease(UUID leaseId) {
        UUID hostelId = getCurrentHostelId();
        LeaseContract lease = leaseContractRepository.findById(leaseId)
            .orElseThrow(() -> new RuntimeException("Lease not found"));
        if (!lease.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Lease does not belong to your hostel");
        }
        lease.setIsActive(false);
        leaseContractRepository.save(lease);
        log.info("Lease soft-deleted: {} in hostel: {}", leaseId, hostelId);
    }

    private LeaseResponseDTO mapToDTO(LeaseContract lc) {
        return LeaseResponseDTO.builder()
            .id(lc.getId())
            .hostelId(lc.getHostel().getId())
            .studentId(lc.getStudent().getId())
            .studentName(lc.getStudent().getFullName())
            .roomId(lc.getRoom().getId())
            .roomNumber(lc.getRoom().getRoomNumber())
            .startDate(lc.getStartDate())
            .endDate(lc.getEndDate())
            .agreedMonthlyRent(lc.getAgreedMonthlyRent())
            .billingAnchorDate(lc.getBillingAnchorDate())
            .paymentTerm(lc.getPaymentTerm().name())
            .isActive(lc.getIsActive())
            .createdAt(lc.getCreatedAt())
            .build();
    }
}
