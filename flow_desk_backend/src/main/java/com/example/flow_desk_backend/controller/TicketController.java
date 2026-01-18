package com.example.flow_desk_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.flow_desk_backend.dto.AssignEmployeeRequest;
import com.example.flow_desk_backend.dto.TicketResponse;
import com.example.flow_desk_backend.dto.UpdateTicketStatusRequest;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employee/ticket")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;

    @PostMapping("/add-ticket")
    private ResponseEntity<TicketEntity> addTicket(@RequestBody TicketEntity newTicket){
        TicketEntity savedTicket = ticketService.addTicket(newTicket);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
    }

    @PutMapping("/assign-employee")
    private ResponseEntity<TicketResponse> assignEmployee(@RequestBody AssignEmployeeRequest request ){
        TicketResponse savedTicket = ticketService.assignEmployee(request.employee_id(), request.ticket_id());
        return ResponseEntity.ok(savedTicket);
    }

    @PutMapping("/update-ticket-status")
    private ResponseEntity<TicketResponse> updateTicketStatus(@RequestBody UpdateTicketStatusRequest request){
        TicketResponse updatedTicket = ticketService.updateTicketStatus(request.ticket_id(), request.status());
        return ResponseEntity.ok(updatedTicket);
    }

    @GetMapping("/{ticket_id}")
    private ResponseEntity<TicketResponse> getTicketById(@PathVariable Long ticket_id){
        TicketResponse existingTicket = ticketService.getTicketById(ticket_id);
        return ResponseEntity.ok(existingTicket);
    }

    @GetMapping("/all")
    private ResponseEntity<List<TicketEntity>> getTicketById(){
        List<TicketEntity> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
}
