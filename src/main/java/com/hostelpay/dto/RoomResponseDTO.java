package com.hostelpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {
    private UUID id;
    private UUID hostelId;
    private String roomNumber;
    private Integer capacity;
    private BigDecimal defaultPrice;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
