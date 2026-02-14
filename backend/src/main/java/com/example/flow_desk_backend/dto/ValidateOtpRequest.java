package com.example.flow_desk_backend.dto;

public record ValidateOtpRequest (
        String employeeEmail,
        String otp
){}
