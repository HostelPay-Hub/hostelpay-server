package com.hostelpay.controller;

import com.hostelpay.entities.Hostel;
import com.hostelpay.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    @Autowired private AdminService adminService;

    @GetMapping("/hostels")
    public ResponseEntity<List<Map<String, Object>>> getAllHostels() {
        List<Hostel> hostels = adminService.getAllHostels();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Hostel h : hostels) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", h.getId());
            map.put("name", h.getName());
            map.put("address", h.getAddress());
            map.put("ownerEmail", h.getOwner().getEmail());
            map.put("subscriptionActive", h.getSubscriptionActive());
            map.put("isActive", h.getIsActive());
            map.put("createdAt", h.getCreatedAt());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/hostels/{id}/subscription")
    public ResponseEntity<String> toggleSubscription(@PathVariable UUID id, @RequestParam boolean active) {
        adminService.toggleSubscription(id, active);
        return ResponseEntity.ok("Subscription updated to: " + active);
    }
}
