package com.hostelpay.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lease_contracts")
@SQLRestriction("is_active = true")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaseContract extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "agreed_monthly_rent", nullable = false)
    private BigDecimal agreedMonthlyRent;

    @Column(name = "billing_anchor_date", nullable = false)
    private Integer billingAnchorDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_term", nullable = false)
    private PaymentTerm paymentTerm = PaymentTerm.ARREARS;

    public enum PaymentTerm {
        ADVANCE, ARREARS
    }
}
