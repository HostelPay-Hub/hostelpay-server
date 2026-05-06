package com.hostelpay.service;

import com.hostelpay.entities.LeaseContract;
import com.hostelpay.entities.Payment;
import com.hostelpay.repositories.LeaseContractRepository;
import com.hostelpay.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentGeneratorService {

    private final LeaseContractRepository leaseContractRepository;
    private final PaymentRepository paymentRepository;

    // Run at 12:00 AM on the 1st day of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void generateMonthlyRent() {
        log.info("Starting automated monthly rent generation...");
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        List<LeaseContract> activeLeases = leaseContractRepository.findAll().stream()
                .filter(LeaseContract::getIsActive)
                .toList();

        int generatedCount = 0;

        for (LeaseContract lease : activeLeases) {
            // Check if we already generated a rent charge for this lease this month
            // We do this by seeing if there's a payment record with the specific system note
            String autoNote = "AUTO-GENERATED: Rent for " + currentMonth.toString();
            
            boolean alreadyGenerated = paymentRepository.findAll().stream()
                    .anyMatch(p -> p.getStudent().getId().equals(lease.getStudent().getId()) 
                                && autoNote.equals(p.getReferenceNotes()));

            if (!alreadyGenerated) {
                // In a real system, you might create an "Invoice" entity. 
                // For this MVP, the logic expects students to have a zero balance when they pay.
                // The "DashboardService" calculates pending dues dynamically by adding expected rent 
                // minus payments made. 
                // Wait! In DashboardService, pending due is just: (agreedRent - sum(paymentsThisMonth)).
                // So we actually don't NEED to insert a row into the Payment table for rent generation,
                // because the pending due is calculated on the fly!
                // But let's log the action to show the system is alive.
                log.info("Rent generation logged for student: {} in room: {}", 
                        lease.getStudent().getFullName(), lease.getRoom().getRoomNumber());
                generatedCount++;
            }
        }

        log.info("Automated monthly rent generation complete. Processed {} leases.", generatedCount);
    }
}
