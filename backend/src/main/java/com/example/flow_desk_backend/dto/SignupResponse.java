package com.example.flow_desk_backend.dto;

public record SignupResponse(
    Long userId,
    String email,
    String role,
    String message
) {}
