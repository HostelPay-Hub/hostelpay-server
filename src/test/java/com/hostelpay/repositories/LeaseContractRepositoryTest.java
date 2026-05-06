package com.hostelpay.repositories;

import com.hostelpay.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class LeaseContractRepositoryTest {

    @Autowired
    private LeaseContractRepository leaseContractRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    private User testOwner;
    private Hostel testHostel;
    private Room testRoom;
    private Student testStudent;
    private LeaseContract testLeaseContract;

    @BeforeEach
    public void setUp() {
        testOwner = User.builder()
            .email("owner@example.com")
            .phoneNumber("9876543210")
            .passwordHash("hashed_password_123")
            .role(User.UserRole.OWNER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testOwner = userRepository.save(testOwner);

        testHostel = Hostel.builder()
            .owner(testOwner)
            .name("Test Hostel")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testHostel = hostelRepository.save(testHostel);

        testRoom = Room.builder()
            .hostel(testHostel)
            .roomNumber("101")
            .capacity(2)
            .defaultPrice(new BigDecimal("5000.00"))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testRoom = roomRepository.save(testRoom);

        testStudent = Student.builder()
            .hostel(testHostel)
            .fullName("John Doe")
            .phoneNumber("9000000001")
            .dob(LocalDate.of(2000, 5, 15))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testStudent = studentRepository.save(testStudent);

        testLeaseContract = LeaseContract.builder()
            .hostel(testHostel)
            .student(testStudent)
            .room(testRoom)
            .startDate(LocalDate.now().minusDays(5))
            .endDate(null)
            .agreedMonthlyRent(new BigDecimal("5000.00"))
            .billingAnchorDate(5)
            .paymentTerm(LeaseContract.PaymentTerm.ARREARS)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    public void testSaveLeaseContract() {
        LeaseContract savedContract = leaseContractRepository.save(testLeaseContract);

        assertNotNull(savedContract.getId());
        assertEquals(testStudent.getId(), savedContract.getStudent().getId());
        assertEquals(5, savedContract.getBillingAnchorDate());
    }

    @Test
    public void testFindContractsByStudentId() {
        leaseContractRepository.save(testLeaseContract);

        List<LeaseContract> contracts = leaseContractRepository.findByStudentId(testStudent.getId());

        assertTrue(contracts.size() > 0);
        assertEquals(testStudent.getId(), contracts.get(0).getStudent().getId());
    }

    @Test
    public void testFindContractsByHostelId() {
        leaseContractRepository.save(testLeaseContract);

        List<LeaseContract> contracts = leaseContractRepository.findByHostelId(testHostel.getId());

        assertTrue(contracts.size() > 0);
    }

    @Test
    public void testSoftDeleteFilter() {
        LeaseContract savedContract = leaseContractRepository.save(testLeaseContract);
        leaseContractRepository.flush();

        savedContract.setIsActive(false);
        leaseContractRepository.save(savedContract);
        leaseContractRepository.flush();

        List<LeaseContract> activeContracts = leaseContractRepository.findActiveContractsByHostelId(testHostel.getId());

        assertEquals(0, activeContracts.size());
    }

    @Test
    public void testFindContractsOnSpecificDate() {
        LeaseContract savedContract = leaseContractRepository.save(testLeaseContract);

        LocalDate queryDate = LocalDate.now();
        List<LeaseContract> contractsOnDate = leaseContractRepository.findActiveContractsOnDate(testHostel.getId(), queryDate);

        assertTrue(contractsOnDate.size() > 0);
    }

}
