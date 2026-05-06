package com.hostelpay.controller;

import com.hostelpay.dto.CreateLeaseRequestDTO;
import com.hostelpay.dto.LeaseResponseDTO;
import com.hostelpay.service.LeaseContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leases")
public class LeaseContractController {

    @Autowired private LeaseContractService leaseContractService;

    @PostMapping
    public ResponseEntity<LeaseResponseDTO> create(@Valid @RequestBody CreateLeaseRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseContractService.createLease(req));
    }

    @GetMapping
    public ResponseEntity<List<LeaseResponseDTO>> getAll() {
        return ResponseEntity.ok(leaseContractService.getAllLeases());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<LeaseResponseDTO>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(leaseContractService.getLeasesByStudent(studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        leaseContractService.deleteLease(id);
        return ResponseEntity.ok("Lease deleted");
    }
}
