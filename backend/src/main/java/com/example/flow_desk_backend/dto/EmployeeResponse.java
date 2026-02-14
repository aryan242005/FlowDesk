package com.example.flow_desk_backend.dto;

import java.time.LocalDateTime;

import com.example.flow_desk_backend.entity.Role;

public record EmployeeResponse (
    Long id,
    String employeeName,
    String employeeEmail,
    String department,
    String designation,
    String phoneNumber,
    Role role,
    Boolean isActive,
    LocalDateTime createdAt
){}
