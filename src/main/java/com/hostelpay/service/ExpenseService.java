package com.hostelpay.service;

import com.hostelpay.dto.ExpenseDTO;
import com.hostelpay.entities.Expense;
import com.hostelpay.entities.Hostel;
import com.hostelpay.repositories.ExpenseRepository;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final HostelRepository hostelRepository;

    private UUID getCurrentHostelId() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getHostelId();
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findByHostelIdOrderByExpenseDateDesc(getCurrentHostelId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO dto) {
        Hostel hostel = hostelRepository.findById(getCurrentHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        Expense expense = Expense.builder()
                .hostel(hostel)
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .expenseDate(dto.getExpenseDate())
                .receiptUrl(dto.getReceiptUrl())
                .build();

        return mapToDTO(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getHostel().getId().equals(getCurrentHostelId())) {
            throw new AccessDeniedException("Access denied");
        }

        expense.setIsActive(false);
        expenseRepository.save(expense);
    }

    private ExpenseDTO mapToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setReceiptUrl(expense.getReceiptUrl());
        return dto;
    }
}
