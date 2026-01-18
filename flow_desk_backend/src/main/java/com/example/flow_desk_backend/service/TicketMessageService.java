package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.MarkAsReadRequest;
import com.example.flow_desk_backend.dto.TicketMessageResponse;
import com.example.flow_desk_backend.entity.AuthorType;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.TicketMessageEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.exception.TicketNotFoundException;
import com.example.flow_desk_backend.repository.TicketMessageRepository;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository ticketMessageRepository;

    public TicketMessageResponse addTicketMessage(Long ticketId, String message, String ticketAccessToken){
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        TicketMessageEntity newMessage = new TicketMessageEntity();
        if(ticketAccessToken == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            UserEntity loggedInUser = principal.getUser();

            newMessage.setTicket(ticket);
            newMessage.setMessageText(message);
            newMessage.setAuthorType(AuthorType.EMPLOYEE);
            newMessage.setAuthorId(loggedInUser.getId());
            newMessage.setAuthorName(loggedInUser.getEmployeeName());

        }else{
            newMessage.setTicket(ticket);
            newMessage.setMessageText(message);
            newMessage.setAuthorId(ticket.getCustomer().getCustomerId());
            newMessage.setAuthorName(ticket.getCustomer().getCustomerName());
            newMessage.setAuthorType(AuthorType.CUSTOMER);
        }
        TicketMessageEntity savedMessage = ticketMessageRepository.save(newMessage);

        return new TicketMessageResponse(
                newMessage.getTicketMessageId(),
                newMessage.getMessageText(),
                newMessage.getAuthorName(),
                newMessage.getCreatedAt()
        );
    }

    public List<TicketMessageResponse> getMessagesByTicket(Long ticketId){
        List<TicketMessageEntity> ticketMessages = ticketMessageRepository.findByTicketTicketId(ticketId);

        return ticketMessages.stream().map(ticketMessage -> new TicketMessageResponse(
                ticketMessage.getTicketMessageId(),
                ticketMessage.getMessageText(),
                ticketMessage.getAuthorName(),
                ticketMessage.getCreatedAt()
        )).toList();
    }

    public String markAllAsRead(Long ticketId, AuthorType authorType){
        List<TicketMessageEntity> messages = ticketMessageRepository.markAsRead(ticketId, authorType);
        return "Marked as read";
    }
}
