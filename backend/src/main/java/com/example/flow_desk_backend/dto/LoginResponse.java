package com.example.flow_desk_backend.dto;

public record LoginResponse(
    Long userId,
    String email,
    String role,
    String token,
    String message
){}
