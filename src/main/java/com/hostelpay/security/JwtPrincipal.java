package com.hostelpay.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class JwtPrincipal {
    private UUID userId;
    private String email;
    private UUID hostelId;
    private String role;

    @Override
    public String toString() {
        return email;
    }
}
