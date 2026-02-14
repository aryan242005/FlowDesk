package com.example.flow_desk_backend.dto;

public record GetTicketMessageFileRequest(
        String fileName,
        Long ticketId
) {}