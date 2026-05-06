package com.hostelpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
    private BigDecimal totalRevenueThisMonth;
    private BigDecimal totalPendingDues;
    private BigDecimal totalExpensesThisMonth;
    private BigDecimal netProfitThisMonth;
    private long activeStudents;
    private long totalRooms;
    private long activeLeases;
}
