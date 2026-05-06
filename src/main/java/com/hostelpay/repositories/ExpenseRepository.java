package com.hostelpay.repositories;

import com.hostelpay.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByHostelIdOrderByExpenseDateDesc(UUID hostelId);
}
