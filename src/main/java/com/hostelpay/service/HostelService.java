package com.hostelpay.service;

import com.hostelpay.dto.HostelSettingsDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.security.JwtPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class HostelService {

    @Autowired private HostelRepository hostelRepository;

    public Hostel updateMyHostel(HostelSettingsDTO dto) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID hostelId = principal.getHostelId();
        
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));
            
        if (dto.getName() != null) hostel.setName(dto.getName());
        if (dto.getAddress() != null) hostel.setAddress(dto.getAddress());
        if (dto.getWhatsappGroupUrl() != null) hostel.setWhatsappGroupUrl(dto.getWhatsappGroupUrl());
        
        return hostelRepository.save(hostel);
    }
    
    public Hostel getMyHostel() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return hostelRepository.findById(principal.getHostelId())
            .orElseThrow(() -> new RuntimeException("Hostel not found"));
    }
}
