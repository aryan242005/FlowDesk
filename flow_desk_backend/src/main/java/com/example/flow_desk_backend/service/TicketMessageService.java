package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.entity.AuthorType;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.TicketMessageEntity;
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

    public TicketMessageResponse addTicketMessage(Long ticketId, String messageText, MultipartFile file, String customerToken){
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        TicketMessageEntity newMessage = new TicketMessageEntity();
        if(customerToken == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            UserEntity loggedInUser = principal.getUser();

            newMessage.setTicket(ticket);
            newMessage.setMessageText(messageText);
            newMessage.setAuthorType(AuthorType.EMPLOYEE);
            newMessage.setAuthorId(loggedInUser.getId());
            newMessage.setAuthorName(loggedInUser.getEmployeeName());

        }else{
            String customerEmail = redisService.get(customerToken);
            if(customerEmail == null) throw new InvalidTokenException();

            if(!ticket.getCustomer().getEmail().equals(customerEmail)) throw new AuthorizationException("\"Not authorized to add message to this ticket\"\n");

            newMessage.setTicket(ticket);
            newMessage.setMessageText(messageText);
            newMessage.setAuthorId(ticket.getCustomer().getCustomerId());
            newMessage.setAuthorName(ticket.getCustomer().getCustomerName());
            newMessage.setAuthorType(AuthorType.CUSTOMER);
        }

        if(file != null && !file.isEmpty()){
            String path = fileService.store(file);
            newMessage.setAttachmentName(file.getOriginalFilename());
            newMessage.setAttachmentType(file.getContentType());
            newMessage.setAttachmentSize(file.getSize());
            newMessage.setAttachmentPath(path);
        }
        TicketMessageEntity savedMessage = ticketMessageRepository.save(newMessage);

        return mapToDto(savedMessage);
    }

    public List<TicketMessageResponse> getMessagesByTicket(Long ticketId){
        List<TicketMessageEntity> ticketMessages = ticketMessageRepository.findByTicketTicketId(ticketId);

        return ticketMessages.stream().map(this::mapToDto).toList();
    }

    public String markAllAsRead(Long ticketId, AuthorType authorType){
        List<TicketMessageEntity> messages = ticketMessageRepository.markAsRead(ticketId, authorType);
        return "Marked as read";
    }

    public TicketMessageResponse mapToDto(TicketMessageEntity ticketMessage){
        return new TicketMessageResponse(
                ticketMessage.getTicketMessageId(),
                ticketMessage.getMessageText(),
                ticketMessage.getAuthorName(),
                ticketMessage.getCreatedAt()
        );
    }
}
