package com.hostelpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaseResponseDTO {
    private UUID id;
    private UUID hostelId;
    private UUID studentId;
    private String studentName;
    private UUID roomId;
    private String roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal agreedMonthlyRent;
    private Integer billingAnchorDate;
    private String paymentTerm;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
