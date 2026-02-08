package com.example.flow_desk_backend.controller;

import com.example.flow_desk_backend.dto.MarkAsReadRequest;
import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.service.FileService;
import com.example.flow_desk_backend.service.TicketMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class TicketMessageController {

    private final TicketMessageService ticketMessageService;
    private final FileService fileService;

    @PostMapping(value="/create", consumes = "multipart/form-data")
    public ResponseEntity<TicketMessageResponse> addTicketMessage(@RequestParam Long ticketId, @RequestParam String messageText, MultipartFile file, String customerToken){
        return ResponseEntity.ok(ticketMessageService.addTicketMessage(ticketId, messageText, file));
    }

    @GetMapping("/files/download")
    public ResponseEntity<Resource> getTicketMessageFile(@RequestParam String fileName, @RequestParam Long ticketId, @RequestParam String customerToken) throws MalformedURLException {
        return ResponseEntity.ok(fileService.download(fileName, ticketId, customerToken));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketMessageResponse>> getMessagesByTicket(@PathVariable Long ticketId){
        return ResponseEntity.ok(ticketMessageService.getMessagesByTicket(ticketId));
    }

//    @PutMapping("/markAsRead")
//    public ResponseEntity<String> markAllAsRead(@RequestBody MarkAsReadRequest request){
//        return ResponseEntity.ok(ticketMessageService.markAllAsRead(request.ticketId(), request.authorType()));
//    }
}
