package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.entity.ProjectStatus;
import com.example.flow_desk_backend.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long projectId,
        String projectName,
        String projectDescription,
        String projectCode,
        EmployeeResponse createdBy,
        List<EmployeeResponse> projectMembers,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}