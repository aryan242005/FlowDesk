package com.example.flow_desk_backend.dto;

public record CompanyResponse(
        Long companyId,
        String companyName,
        String companyAddress,
        String companyEmail
) {}
