package com.hostelpay.controller;

import com.hostelpay.dto.CreateRoomRequestDTO;
import com.hostelpay.dto.RoomResponseDTO;
import com.hostelpay.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired private RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody CreateRoomRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(req));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAll() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CreateRoomRequestDTO req) {
        return ResponseEntity.ok(roomService.updateRoom(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted");
    }
}
