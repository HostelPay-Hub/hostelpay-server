package com.hostelpay.service;

import com.hostelpay.entities.Hostel;
import com.hostelpay.repositories.HostelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class AdminService {

    @Autowired
    private HostelRepository hostelRepository;

    public List<Hostel> getAllHostels() {
        return hostelRepository.findAllHostelsIncludingInactive();
    }

    public void toggleSubscription(UUID hostelId, boolean active) {
        Hostel hostel = hostelRepository.findByIdIncludingInactive(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));
        hostel.setSubscriptionActive(active);
        hostelRepository.save(hostel);
        log.info("Hostel {} subscription set to: {}", hostelId, active);
    }
}
