package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.TicketEntity;

public record CreateTicketRequest(
   TicketEntity newTicket,
   Long companyId,
   String customerToken
) {}
