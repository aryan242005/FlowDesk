package com.example.flow_desk_backend.dto;

public record UpdatePasswordRequest (
        String resetToken,
        String newPassword
){}
