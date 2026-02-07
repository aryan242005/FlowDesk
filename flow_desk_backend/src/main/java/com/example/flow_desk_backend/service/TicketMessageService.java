package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.TicketCommentEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.exception.AuthorizationException;
import com.example.flow_desk_backend.exception.InvalidTokenException;
import com.example.flow_desk_backend.exception.TicketNotFoundException;
import com.example.flow_desk_backend.repository.TicketMessageRepository;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import com.example.flow_desk_backend.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final RedisService redisService;
    private final FileService fileService;

    public TicketMessageResponse addTicketMessage(Long ticketId, String messageText, MultipartFile file){
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        TicketCommentEntity newComment = new TicketCommentEntity();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        newComment.setTicket(ticket);
        newComment.setCreatedBy(loggedInUser);
        newComment.setCommentText(messageText);

        if(file != null && !file.isEmpty()){
            String path = fileService.store(file);
            newComment.setAttachmentName(file.getOriginalFilename());
            newComment.setAttachmentType(file.getContentType());
            newComment.setAttachmentSize(file.getSize());
            newComment.setAttachmentPath(path);
        }
        TicketCommentEntity savedMessage = ticketMessageRepository.save(newComment);

        return mapToDto(savedMessage);
    }

    public List<TicketMessageResponse> getMessagesByTicket(Long ticketId){
        List<TicketCommentEntity> ticketMessages = ticketMessageRepository.findByTicketTicketId(ticketId);

        return ticketMessages.stream().map(this::mapToDto).toList();
    }

//    public String markAllAsRead(Long ticketId, AuthorType authorType){
//        List<TicketCommentEntity> messages = ticketMessageRepository.markAsRead(ticketId, authorType);
//        return "Marked as read";
//    }

    public TicketMessageResponse mapToDto(TicketCommentEntity ticketMessage){
        return new TicketMessageResponse(
                ticketMessage.getTicketCommentId(),
                ticketMessage.getCommentText(),
                ticketMessage.getCreatedBy().getEmployeeName(),
                ticketMessage.getCreatedAt()
        );
    }
}
