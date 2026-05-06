package com.hostelpay.service;

import com.hostelpay.dto.CreateRoomRequestDTO;
import com.hostelpay.dto.RoomResponseDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.Room;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.repositories.RoomRepository;
import com.hostelpay.security.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostelRepository hostelRepository;

    private UUID getCurrentHostelId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal) {
            return ((JwtPrincipal) auth.getPrincipal()).getHostelId();
        }
        throw new RuntimeException("Unauthorized: hostelId not found in token");
    }

    public RoomResponseDTO createRoom(CreateRoomRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));

        if (roomRepository.existsByHostelIdAndRoomNumber(hostelId, request.getRoomNumber())) {
            throw new RuntimeException("Room number " + request.getRoomNumber() + " already exists in this hostel");
        }

        Room room = new Room();
        room.setHostel(hostel);
        room.setRoomNumber(request.getRoomNumber());
        room.setCapacity(request.getCapacity());
        room.setDefaultPrice(request.getDefaultPrice());
        room.setIsActive(true);

        Room saved = roomRepository.save(room);
        log.info("Room created: {} in hostel: {}", saved.getRoomNumber(), hostelId);
        return mapToDTO(saved);
    }

    public List<RoomResponseDTO> getAllRooms() {
        UUID hostelId = getCurrentHostelId();
        return roomRepository.findActiveRoomsByHostelId(hostelId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public RoomResponseDTO updateRoom(UUID roomId, CreateRoomRequestDTO request) {
        UUID hostelId = getCurrentHostelId();
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Room does not belong to your hostel");
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setCapacity(request.getCapacity());
        room.setDefaultPrice(request.getDefaultPrice());

        Room updated = roomRepository.save(room);
        log.info("Room updated: {} in hostel: {}", roomId, hostelId);
        return mapToDTO(updated);
    }

    public void deleteRoom(UUID roomId) {
        UUID hostelId = getCurrentHostelId();
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getHostel().getId().equals(hostelId)) {
            throw new RuntimeException("Unauthorized: Room does not belong to your hostel");
        }
        room.setIsActive(false);
        roomRepository.save(room);
        log.info("Room soft-deleted: {} in hostel: {}", roomId, hostelId);
    }

    private RoomResponseDTO mapToDTO(Room room) {
        return RoomResponseDTO.builder()
            .id(room.getId())
            .hostelId(room.getHostel().getId())
            .roomNumber(room.getRoomNumber())
            .capacity(room.getCapacity())
            .defaultPrice(room.getDefaultPrice())
            .isActive(room.getIsActive())
            .createdAt(room.getCreatedAt())
            .build();
    }
}
