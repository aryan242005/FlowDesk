package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.ProjectEntity;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.TicketPriority;

public record CreateTicketRequest(
        Long projectId,
        String title,
        String description,
        TicketPriority priority
) {}
