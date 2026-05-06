package com.hostelpay.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StudentDashboardDTO {
    private String studentName;
    private String roomNumber;
    private String hostelName;
    private BigDecimal monthlyRent;
    private BigDecimal pendingBalance;
    private String status; // "All Clear" or "Overdue"
    private List<String> roommates;
    private List<PaymentResponseDTO> recentPayments;
}
