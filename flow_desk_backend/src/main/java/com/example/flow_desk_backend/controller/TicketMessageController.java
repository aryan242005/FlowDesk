package com.example.flow_desk_backend.controller;

import com.example.flow_desk_backend.dto.*;
import com.example.flow_desk_backend.service.FileService;
import com.example.flow_desk_backend.service.TicketCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class TicketMessageController {

    private final TicketCommentService ticketCommentService;
    private final FileService fileService;

    @PostMapping(value="/create", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<TicketMessageResponse>> addTicketMessage(AddTicketMessageRequest request){
        ApiResponse<TicketMessageResponse> response = new ApiResponse<>(
                true,
                "Ticket message added successfully",
                ticketCommentService.addTicketMessage(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/files/download")
    public ResponseEntity<ApiResponse<Resource>> getTicketMessageFile(GetTicketMessageFileRequest request) throws MalformedURLException {
        ApiResponse<Resource> response = new ApiResponse<>(
                true,
                "File found successfully",
                fileService.download(request)
        );

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<ApiResponse<List<TicketMessageResponse>>> getMessagesByTicket(@PathVariable Long ticketId){
        List<TicketMessageResponse> tickets = ticketCommentService.getMessagesByTicket(ticketId);
        ApiResponse<List<TicketMessageResponse>> response = new ApiResponse<>(
                true,
                tickets.size() + " messages found and fetched successfully",
                tickets
        );

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
}
