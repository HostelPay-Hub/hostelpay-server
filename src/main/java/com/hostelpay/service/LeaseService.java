package com.hostelpay.service;

import com.hostelpay.dto.CreateLeaseRequestDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.LeaseContract;
import com.hostelpay.entities.Room;
import com.hostelpay.entities.Student;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.repositories.LeaseContractRepository;
import com.hostelpay.repositories.RoomRepository;
import com.hostelpay.repositories.StudentRepository;
import com.hostelpay.security.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class LeaseService {

    @Autowired
    private LeaseContractRepository leaseRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HostelRepository hostelRepository;

    private UUID getCurrentHostelId() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getHostelId();
    }

    public void assignStudentToRoom(CreateLeaseRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));
            
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found"));
            
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check room capacity
        long currentOccupants = leaseRepository.countActiveLeasesByRoomId(room.getId());
        if (currentOccupants >= room.getCapacity()) {
            throw new RuntimeException("Room is already full! Capacity: " + room.getCapacity());
        }

        // Close any existing active leases for this student
        leaseRepository.deactivateAllLeasesForStudent(student.getId());

        LeaseContract lease = LeaseContract.builder()
            .hostel(hostel)
            .student(student)
            .room(room)
            .startDate(request.getStartDate())
            .agreedMonthlyRent(request.getAgreedMonthlyRent())
            .billingAnchorDate(request.getBillingAnchorDate() != null ? request.getBillingAnchorDate() : 1)
            .paymentTerm(LeaseContract.PaymentTerm.ADVANCE)
            .build();
            
        lease.setIsActive(true);
        leaseRepository.save(lease);
        
        log.info("Assigned student {} to room {} in hostel {}", student.getId(), room.getId(), hostelId);
    }
}
