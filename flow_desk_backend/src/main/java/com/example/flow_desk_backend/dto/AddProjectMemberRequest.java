package com.example.flow_desk_backend.dto;

public record AddProjectMemberRequest(
        Long projectId,
        Long userId
) {}