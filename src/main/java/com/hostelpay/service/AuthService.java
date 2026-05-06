package com.hostelpay.service;

import com.hostelpay.dto.AuthResponseDTO;
import com.hostelpay.dto.LoginRequestDTO;
import com.hostelpay.dto.OwnerRegistrationDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.User;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.repositories.UserRepository;
import com.hostelpay.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Login with email and password
     */
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        UUID hostelId = null;
        if (user.getRole() == User.UserRole.OWNER) {
            if (user.getHostels() == null || user.getHostels().isEmpty()) {
                // Auto-fix: Create a default hostel if missing
                log.info("Auto-fixing missing hostel for user: {}", user.getEmail());
                Hostel fixHostel = new Hostel();
                fixHostel.setOwner(user);
                fixHostel.setName("My Hostel");
                fixHostel.setAddress("Please update your address in settings");
                fixHostel.setSubscriptionActive(true);
                fixHostel.setIsActive(true);
                Hostel saved = hostelRepository.save(fixHostel);
                hostelId = saved.getId();
            } else {
                hostelId = user.getHostels().iterator().next().getId();
            }
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), hostelId, user.getRole().name());

        log.info("User logged in: {} role: {} hostel: {}", user.getEmail(), user.getRole(), hostelId);

        return AuthResponseDTO.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .hostelId(hostelId)
            .role(user.getRole().name())
            .token(token)
            .message("Login successful")
            .build();
    }

    /**
     * Register a new owner with their first hostel
     */
    public AuthResponseDTO registerOwner(OwnerRegistrationDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User newUser = User.builder()
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(User.UserRole.OWNER)
            .build();

        User savedUser = userRepository.save(newUser);

        Hostel hostel = new Hostel();
        hostel.setOwner(savedUser);
        hostel.setName(request.getHostelName());
        hostel.setAddress(request.getAddress());
        hostel.setSubscriptionActive(true);
        hostel.setIsActive(true);

        Hostel savedHostel = hostelRepository.save(hostel);

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedHostel.getId(), savedUser.getRole().name());

        log.info("New owner registered: {} hostel: {}", savedUser.getEmail(), savedHostel.getId());

        return AuthResponseDTO.builder()
            .userId(savedUser.getId())
            .email(savedUser.getEmail())
            .hostelId(savedHostel.getId())
            .role(savedUser.getRole().name())
            .token(token)
            .message("Registration successful")
            .build();
    }
}
