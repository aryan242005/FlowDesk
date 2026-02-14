package com.example.flow_desk_backend.dto;

public record AssignEmployeeRequest (
    Long employee_id,
    Long ticket_id
){}
