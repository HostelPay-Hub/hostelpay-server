package com.hostelpay.controller;

import com.hostelpay.dto.DashboardMetricsDTO;
import com.hostelpay.dto.PendingDueDTO;
import com.hostelpay.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DashboardService dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(dashboardService.getMetrics());
    }

    @GetMapping("/pending-dues")
    public ResponseEntity<List<PendingDueDTO>> getPendingDues() {
        return ResponseEntity.ok(dashboardService.getPendingDues());
    }
}
