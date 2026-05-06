package com.hostelpay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class RecordPaymentRequestDTO {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, UPI, BANK_TRANSFER

    private String referenceNotes;
}
