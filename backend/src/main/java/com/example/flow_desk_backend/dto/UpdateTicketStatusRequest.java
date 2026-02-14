package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.TicketStatus;

public record UpdateTicketStatusRequest (
    Long ticket_id,
    TicketStatus status
){}
