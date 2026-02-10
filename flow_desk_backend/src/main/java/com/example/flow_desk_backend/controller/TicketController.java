package com.example.flow_desk_backend.controller;

import java.util.List;

import com.example.flow_desk_backend.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;

    @PostMapping("member/add-ticket")
    private ResponseEntity<ApiResponse<TicketResponse>> createTicket(@RequestBody CreateTicketRequest request){
        ApiResponse<TicketResponse> response = new ApiResponse<>(
                true,
                "Ticket created successfully",
                ticketService.createTicket(request.projectId(), request.title(), request.description(), request.priority())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("manager/assign-employee")
    private ResponseEntity<ApiResponse<TicketResponse>> assignEmployee(@RequestBody AssignEmployeeRequest request ){
        ApiResponse<TicketResponse> response = new ApiResponse<>(
                true,
                "Employee assigned successfully",
                ticketService.assignEmployee(request.employee_id(), request.ticket_id())
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("member/update-ticket-status")
    private ResponseEntity<ApiResponse<TicketResponse>> updateTicketStatus(@RequestBody UpdateTicketStatusRequest request){
        ApiResponse<TicketResponse> response = new ApiResponse<>(
                true,
                "Ticket status updated successfully",
                ticketService.updateTicketStatus(request.ticket_id(), request.status())
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("member/{ticket_id}")
    private ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable Long ticket_id){
        ApiResponse<TicketResponse> response = new ApiResponse<>(
                true,
                "Ticket with " + ticket_id + " fetched successfully",
                ticketService.getTicketById(ticket_id)
        );
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @GetMapping("member/all/{companyId}/{projectId}")
    private ResponseEntity<ApiResponse<List<TicketResponse>>> getAllProjectTickets(@RequestParam Long companyId, @RequestParam Long projectId){
        List<TicketResponse> tickets = ticketService.getAllProjectTickets(companyId, projectId);
        ApiResponse<List<TicketResponse>> response = new ApiResponse<>(
                true,
                tickets.size() + " tickets found and fetched successfully",
                tickets
        );
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
}
