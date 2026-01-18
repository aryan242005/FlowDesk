package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.AuthorType;

public record MarkAsReadRequest (
        Long ticketId,
        AuthorType authorType
){}
