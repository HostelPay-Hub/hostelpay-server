package com.hostelpay.repositories;

import com.hostelpay.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p WHERE p.hostel.id = :hostelId AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findActivePaymentsByHostelId(@Param("hostelId") UUID hostelId);

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findActivePaymentsByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.hostel.id = :hostelId AND p.isActive = true AND p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumPaymentsByHostelIdAndDateRange(@Param("hostelId") UUID hostelId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.isActive = true AND p.status = 'COMPLETED'")
    BigDecimal sumPaymentsByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.isActive = true AND p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumPaymentsByStudentIdAndDateRange(@Param("studentId") UUID studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
