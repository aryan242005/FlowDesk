package com.example.flow_desk_backend.dto;

public record CreateProjectRequest(
        String projectName,
        String projectDescription,
        String projectCode,
        Long companyId
) {
}
