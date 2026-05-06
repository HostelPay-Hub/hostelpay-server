package com.hostelpay.repositories;

import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.Room;
import com.hostelpay.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    private User testOwner;
    private Hostel testHostel;
    private Room testRoom;

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
    }

    @Test
    public void testSaveRoom() {
        Room savedRoom = roomRepository.save(testRoom);

        assertNotNull(savedRoom.getId());
        assertEquals("101", savedRoom.getRoomNumber());
        assertEquals(2, savedRoom.getCapacity());
    }

    @Test
    public void testFindRoomsByHostelId() {
        roomRepository.save(testRoom);

        List<Room> rooms = roomRepository.findByHostelId(testHostel.getId());

        assertTrue(rooms.size() > 0);
        assertEquals("101", rooms.get(0).getRoomNumber());
    }

    @Test
    public void testFindRoomByHostelAndRoomNumber() {
        roomRepository.save(testRoom);

        var foundRoom = roomRepository.findByHostelIdAndRoomNumber(testHostel.getId(), "101");

        assertTrue(foundRoom.isPresent());
        assertEquals("101", foundRoom.get().getRoomNumber());
    }

    @Test
    public void testSoftDeleteFilter() {
        Room savedRoom = roomRepository.save(testRoom);
        roomRepository.flush();

        savedRoom.setIsActive(false);
        roomRepository.save(savedRoom);
        roomRepository.flush();

        List<Room> activeRooms = roomRepository.findActiveRoomsByHostelId(testHostel.getId());

        assertEquals(0, activeRooms.size());
    }

}
