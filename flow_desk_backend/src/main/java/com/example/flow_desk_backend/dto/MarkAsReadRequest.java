package com.example.flow_desk_backend.dto;

public record MarkAsReadRequest (
        Long ticketId,
        AuthorType authorType
){}
