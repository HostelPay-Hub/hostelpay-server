package com.hostelpay.service;

import com.hostelpay.dto.DashboardMetricsDTO;
import com.hostelpay.dto.PendingDueDTO;
import com.hostelpay.entities.LeaseContract;
import com.hostelpay.repositories.*;
import com.hostelpay.security.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private LeaseContractRepository leaseContractRepository;
    @Autowired private ExpenseRepository expenseRepository;

    private UUID getCurrentHostelId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal p) return p.getHostelId();
        throw new RuntimeException("Unauthorized");
    }

    public DashboardMetricsDTO getMetrics() {
        UUID hostelId = getCurrentHostelId();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        com.hostelpay.entities.Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));

        BigDecimal revenue = paymentRepository.sumPaymentsByHostelIdAndDateRange(hostelId, startOfMonth, endOfMonth);
        long activeStudents = studentRepository.countActiveStudentsByHostelId(hostelId);
        long totalRooms = roomRepository.findActiveRoomsByHostelId(hostelId).size();
        List<LeaseContract> activeLeases = leaseContractRepository.findCurrentActiveLeasesByHostelId(hostelId);

        BigDecimal totalExpectedRent = activeLeases.stream()
            .map(LeaseContract::getAgreedMonthlyRent)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingDues = totalExpectedRent.subtract(revenue);
        if (pendingDues.compareTo(BigDecimal.ZERO) < 0) pendingDues = BigDecimal.ZERO;

        BigDecimal expenses = expenseRepository.findByHostelIdOrderByExpenseDateDesc(hostelId).stream()
            .filter(e -> e.getExpenseDate().getYear() == now.getYear() && e.getExpenseDate().getMonth() == now.getMonth())
            .map(e -> e.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = revenue.subtract(expenses);

        return DashboardMetricsDTO.builder()
            .totalRevenueThisMonth(revenue)
            .totalPendingDues(pendingDues)
            .totalExpensesThisMonth(expenses)
            .netProfitThisMonth(netProfit)
            .activeStudents(activeStudents)
            .totalRooms(totalRooms)
            .activeLeases(activeLeases.size())
            .whatsappGroupUrl(hostel.getWhatsappGroupUrl())
            .build();
    }

    public List<PendingDueDTO> getPendingDues() {
        UUID hostelId = getCurrentHostelId();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<LeaseContract> activeLeases = leaseContractRepository.findCurrentActiveLeasesByHostelId(hostelId);
        List<PendingDueDTO> pendingDues = new ArrayList<>();

        for (LeaseContract lease : activeLeases) {
            BigDecimal paidThisMonth = paymentRepository.sumPaymentsByStudentIdAndDateRange(
                lease.getStudent().getId(), startOfMonth, endOfMonth);
            BigDecimal pending = lease.getAgreedMonthlyRent().subtract(paidThisMonth);

            if (pending.compareTo(BigDecimal.ZERO) > 0) {
                pendingDues.add(PendingDueDTO.builder()
                    .studentId(lease.getStudent().getId())
                    .studentName(lease.getStudent().getFullName())
                    .phoneNumber(lease.getStudent().getPhoneNumber())
                    .roomNumber(lease.getRoom().getRoomNumber())
                    .monthlyRent(lease.getAgreedMonthlyRent())
                    .paidThisMonth(paidThisMonth)
                    .pendingAmount(pending)
                    .build());
            }
        }
        return pendingDues;
    }
}
