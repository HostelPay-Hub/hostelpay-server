package com.hostelpay.controller;

import com.hostelpay.dto.AuthResponseDTO;
import com.hostelpay.dto.LoginRequestDTO;
import com.hostelpay.dto.OwnerRegistrationDTO;
import com.hostelpay.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register-owner")
    public ResponseEntity<AuthResponseDTO> registerOwner(@Valid @RequestBody OwnerRegistrationDTO request) {
        log.info("Registration attempt for: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerOwner(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
