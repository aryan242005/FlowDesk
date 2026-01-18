package com.example.flow_desk_backend.dto;

public record ValidateOtpResponse (
        String message,
        String resetToken
)
{}
