package com.hostelpay.service;

import com.hostelpay.dto.NoticeDTO;
import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.Notice;
import com.hostelpay.repositories.HostelRepository;
import com.hostelpay.repositories.NoticeRepository;
import com.hostelpay.security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final HostelRepository hostelRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private UUID getCurrentHostelId() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getHostelId();
    }

    @Transactional(readOnly = true)
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findByHostelIdOrderByCreatedAtDesc(getCurrentHostelId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeDTO createNotice(NoticeDTO dto) {
        Hostel hostel = hostelRepository.findById(getCurrentHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        Notice notice = Notice.builder()
                .hostel(hostel)
                .title(dto.getTitle())
                .content(dto.getContent())
                .priority(dto.getPriority() != null ? dto.getPriority() : "NORMAL")
                .build();

        Notice saved = noticeRepository.save(notice);

        // Broadcast WebSocket Update
        messagingTemplate.convertAndSend("/topic/hostel/" + hostel.getId() + "/notices", "NEW_NOTICE");

        return mapToDTO(saved);
    }

    @Transactional
    public void deleteNotice(UUID id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        if (!notice.getHostel().getId().equals(getCurrentHostelId())) {
            throw new AccessDeniedException("Access denied");
        }

        notice.setIsActive(false);
        noticeRepository.save(notice);
    }

    private NoticeDTO mapToDTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setPriority(notice.getPriority());
        dto.setCreatedAt(notice.getCreatedAt());
        return dto;
    }
}
