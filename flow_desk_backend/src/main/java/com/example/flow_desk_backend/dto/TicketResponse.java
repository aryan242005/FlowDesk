package com.example.flow_desk_backend.dto;

import java.time.LocalDateTime;

public record TicketResponse (
    Long id,
    String title,
    String description,
    String priority,
    String status,
    LocalDateTime createdAt,
    EmployeeResponse employee
){}

