package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.Status;

public record UpdateTicketStatusRequest (
    Long ticket_id,
    Status status
){}
