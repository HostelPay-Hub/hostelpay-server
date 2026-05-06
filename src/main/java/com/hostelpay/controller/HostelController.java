package com.hostelpay.controller;

import com.hostelpay.dto.HostelSettingsDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hostels")
public class HostelController {

    @Autowired private HostelService hostelService;

    @GetMapping("/me")
    public ResponseEntity<Hostel> getMyHostel() {
        return ResponseEntity.ok(hostelService.getMyHostel());
    }

    @PutMapping("/me")
    public ResponseEntity<Hostel> updateMyHostel(@RequestBody HostelSettingsDTO dto) {
        return ResponseEntity.ok(hostelService.updateMyHostel(dto));
    }
}
