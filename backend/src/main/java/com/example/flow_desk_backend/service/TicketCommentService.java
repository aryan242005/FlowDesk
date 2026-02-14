package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.AddTicketMessageRequest;
import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.entity.AttachmentEntity;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.TicketCommentEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.exception.AuthorizationException;
import com.example.flow_desk_backend.exception.InvalidFileException;
import com.example.flow_desk_backend.exception.TicketNotFoundException;
import com.example.flow_desk_backend.repository.TicketMessageRepository;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCommentService {

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final FileService fileService;

    @Transactional
    public TicketMessageResponse addTicketMessage(AddTicketMessageRequest request){
        // Find ticket for which message is sent
        TicketEntity ticket = ticketRepository.findById(request.ticketId()).orElseThrow(() -> new TicketNotFoundException("Ticket with id " + request.ticketId() + " not found"));
        TicketCommentEntity newComment = getTicketCommentEntity(request, ticket);

        if(request.files() != null && !request.files().isEmpty()){
            for(MultipartFile file : request.files()){
                String path = fileService.store(file);
                AttachmentEntity attachment = getAttachmentEntity(newComment, path, file);
                newComment.getFiles().add(attachment);
            }
        }

        TicketCommentEntity savedMessage = ticketMessageRepository.save(newComment);

        return mapToDto(savedMessage);
    }

    private static TicketCommentEntity getTicketCommentEntity(AddTicketMessageRequest request, TicketEntity ticket) {
        TicketCommentEntity newComment = new TicketCommentEntity();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        if (!(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new AuthorizationException("Invalid user session");
        }
        UserEntity loggedInUser = principal.getUser();

        newComment.setTicket(ticket);
        newComment.setCreatedBy(loggedInUser);
        newComment.setCommentText(request.messageText());
        return newComment;
    }

    private AttachmentEntity getAttachmentEntity(TicketCommentEntity newComment, String path, MultipartFile file){
        AttachmentEntity attachment = new AttachmentEntity();
        attachment.setTicketComment(newComment);

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException("File name missing");
        }
        attachment.setFileName(originalName);


        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            throw new InvalidFileException("Invalid file type");
        }
        attachment.setMimeType(mimeType);
        attachment.setFilePath(path);

        return attachment;
    }

    public List<TicketMessageResponse> getMessagesByTicket(Long ticketId){
        List<TicketCommentEntity> ticketMessages = ticketMessageRepository.findByTicketTicketId(ticketId);

        return ticketMessages.stream().map(this::mapToDto).toList();
    }

    // Utility Functions

    public TicketMessageResponse mapToDto(TicketCommentEntity ticketMessage){
        return new TicketMessageResponse(
                ticketMessage.getTicketCommentId(),
                ticketMessage.getCommentText(),
                ticketMessage.getCreatedBy().getEmployeeName(),
                ticketMessage.getCreatedAt()
        );
    }
}
