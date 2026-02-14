package com.example.flow_desk_backend.dto;

import java.time.LocalDateTime;

public record TicketMessageResponse (
        Long ticketMessageId,
        String messageText,
        String senderName,
        LocalDateTime createdAt
){}
