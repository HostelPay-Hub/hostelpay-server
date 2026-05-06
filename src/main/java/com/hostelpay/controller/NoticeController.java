package com.hostelpay.controller;

import com.hostelpay.dto.NoticeDTO;
import com.hostelpay.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @PostMapping
    public ResponseEntity<NoticeDTO> createNotice(@Valid @RequestBody NoticeDTO dto) {
        return new ResponseEntity<>(noticeService.createNotice(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable UUID id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
