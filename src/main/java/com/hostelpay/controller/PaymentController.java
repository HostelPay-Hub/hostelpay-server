package com.hostelpay.controller;

import com.hostelpay.dto.PaymentResponseDTO;
import com.hostelpay.dto.RecordPaymentRequestDTO;
import com.hostelpay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> record(@Valid @RequestBody RecordPaymentRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.recordPayment(req));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent(studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted");
    }
}
