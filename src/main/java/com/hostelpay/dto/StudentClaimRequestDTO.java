package com.hostelpay.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentClaimRequestDTO {
    private String phoneNumber;
    private LocalDate dob;
    private String password;
    private String email; // Optional but recommended for future notifications
}
