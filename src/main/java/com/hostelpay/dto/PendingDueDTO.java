package com.hostelpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingDueDTO {
    private UUID studentId;
    private String studentName;
    private String phoneNumber;
    private String roomNumber;
    private BigDecimal monthlyRent;
    private BigDecimal paidThisMonth;
    private BigDecimal pendingAmount;
}
