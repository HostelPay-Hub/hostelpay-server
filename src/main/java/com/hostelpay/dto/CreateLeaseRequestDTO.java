package com.hostelpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaseRequestDTO {
    private UUID studentId;
    private UUID roomId;
    private LocalDate startDate;
    private BigDecimal agreedMonthlyRent;
    private Integer billingAnchorDate; // e.g., 5th of every month
}
