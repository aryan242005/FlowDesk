package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.TicketEntity;

public record AddTicketRequest(
        Long ticketId,
        String messageText,
        String ticketAccessToken
) {
}
