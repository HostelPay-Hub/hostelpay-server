package com.hostelpay.controller;

import com.hostelpay.dto.CreateLeaseRequestDTO;
import com.hostelpay.service.LeaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leases")
@PreAuthorize("hasRole('OWNER')")
public class LeaseController {

    @Autowired
    private LeaseService leaseService;

    @PostMapping("/assign")
    public ResponseEntity<String> assignStudent(@RequestBody CreateLeaseRequestDTO request) {
        leaseService.assignStudentToRoom(request);
        return ResponseEntity.ok("Student assigned to room successfully");
    }
}
