package com.hostelpay.repositories;

import com.hostelpay.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
            .email("owner@example.com")
            .phoneNumber("9876543210")
            .passwordHash("hashed_password_123")
            .role(User.UserRole.OWNER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    public void testSaveUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("owner@example.com", savedUser.getEmail());
        assertEquals(User.UserRole.OWNER, savedUser.getRole());
    }

    @Test
    public void testFindUserByEmail() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail("owner@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("owner@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindUserByPhoneNumber() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByPhoneNumber("9876543210");

        assertTrue(foundUser.isPresent());
        assertEquals("9876543210", foundUser.get().getPhoneNumber());
    }

    @Test
    public void testEmailUniqueness() {
        userRepository.save(testUser);

        User duplicateUser = User.builder()
            .email("owner@example.com")
            .phoneNumber("1234567890")
            .passwordHash("hashed_password_456")
            .role(User.UserRole.OWNER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            userRepository.flush();
        });
    }

    @Test
    public void testUpdateUser() {
        User savedUser = userRepository.save(testUser);
        UUID userId = savedUser.getId();

        savedUser.setPhoneNumber("1111111111");
        userRepository.save(savedUser);

        User updatedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("1111111111", updatedUser.getPhoneNumber());
    }

}
