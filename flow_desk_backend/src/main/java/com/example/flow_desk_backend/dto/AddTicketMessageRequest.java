package com.example.flow_desk_backend.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record AddTicketMessageRequest(
        Long ticketId,
        String messageText,
        List<MultipartFile> files,
        String customerToken
) {}