package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.GetTicketMessageFileRequest;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.exception.AuthorizationException;
import com.example.flow_desk_backend.exception.TicketNotFoundException;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import com.example.flow_desk_backend.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Path root = Paths.get("uploads/ticketMessageFiles");
    private final TicketRepository ticketRepository;
    private final RedisService redisService;

    public String store(MultipartFile file) {
        try {
            Files.createDirectories(root);

            String originalName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalName;

            Path destination = root.resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return "ticketMessageFiles/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public Resource download(GetTicketMessageFileRequest request) throws MalformedURLException {
        TicketEntity ticket = ticketRepository.findById(request.ticketId()).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        // Check if current user is member of the project
        if (!ticket.getProject().getProjectMembers().contains(loggedInUser)) {
            throw new AuthorizationException("Only project members can download the ticket files");
        }

        Path filePath = Paths.get("uploads/ticketMessageFiles").resolve(request.fileName());
        return new UrlResource(filePath.toUri());
    }
}
