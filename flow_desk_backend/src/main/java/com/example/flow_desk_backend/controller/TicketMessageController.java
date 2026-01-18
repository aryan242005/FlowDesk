package com.example.flow_desk_backend.controller;

import com.example.flow_desk_backend.dto.AddTicketRequest;
import com.example.flow_desk_backend.dto.MarkAsReadRequest;
import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.service.TicketMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class TicketMessageController {

    private final TicketMessageService ticketMessageService;

    @PostMapping("/create")
    public ResponseEntity<TicketMessageResponse> addTicketMessage(@RequestBody AddTicketRequest request){
        return ResponseEntity.ok(ticketMessageService.addTicketMessage(request.ticketId(), request.messageText(), request.ticketAccessToken()));
    }

    @GetMapping("/get/{ticketId}")
    public ResponseEntity<List<TicketMessageResponse>> getMessagesByTicket(@PathVariable Long ticketId){
        return ResponseEntity.ok(ticketMessageService.getMessagesByTicket(ticketId));
    }

    @PutMapping("/markAsRead")
    public ResponseEntity<String> markAllAsRead(@RequestBody MarkAsReadRequest request){
        return ResponseEntity.ok(ticketMessageService.markAllAsRead(request.ticketId(), request.authorType()));
    }
}
