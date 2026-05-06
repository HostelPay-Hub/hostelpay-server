package com.hostelpay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaseRequestDTO {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Monthly rent is required")
    @Min(value = 1, message = "Rent must be positive")
    private BigDecimal agreedMonthlyRent;

    @NotNull(message = "Billing anchor date is required")
    @Min(value = 1, message = "Billing anchor date must be between 1 and 31")
    private Integer billingAnchorDate;

    private String paymentTerm; // ADVANCE or ARREARS
}
