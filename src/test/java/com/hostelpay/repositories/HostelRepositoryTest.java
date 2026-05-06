package com.hostelpay.repositories;

import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class HostelRepositoryTest {

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    private User testOwner;
    private Hostel testHostel;

    @BeforeEach
    public void setUp() {
        testOwner = User.builder()
            .email("hostel_owner@example.com")
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
            .address("123 Main Street, City")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    public void testSaveHostel() {
        Hostel savedHostel = hostelRepository.save(testHostel);

        assertNotNull(savedHostel.getId());
        assertEquals("Test Hostel", savedHostel.getName());
        assertEquals(testOwner.getId(), savedHostel.getOwner().getId());
    }

    @Test
    public void testFindHostelsByOwnerId() {
        hostelRepository.save(testHostel);

        List<Hostel> hostels = hostelRepository.findByOwnerId(testOwner.getId());

        assertTrue(hostels.size() > 0);
        assertEquals("Test Hostel", hostels.get(0).getName());
    }

    @Test
    public void testSoftDeleteFilter() {
        Hostel savedHostel = hostelRepository.save(testHostel);
        hostelRepository.flush();

        savedHostel.setIsActive(false);
        hostelRepository.save(savedHostel);
        hostelRepository.flush();

        List<Hostel> activeHostels = hostelRepository.findActiveHostelsByOwnerId(testOwner.getId());

        assertEquals(0, activeHostels.size());
    }

    @Test
    public void testUpdateHostel() {
        Hostel savedHostel = hostelRepository.save(testHostel);

        savedHostel.setAddress("456 New Street, City");
        hostelRepository.save(savedHostel);

        Hostel updatedHostel = hostelRepository.findById(savedHostel.getId()).orElse(null);
        assertNotNull(updatedHostel);
        assertEquals("456 New Street, City", updatedHostel.getAddress());
    }

}
