package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ticket_message")
@Getter
@Setter
public class TicketMessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketMessageId;

    @NotNull
    @ManyToOne
    @JoinColumn(name="ticket_id")
    private TicketEntity ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AuthorType authorType;

    @Column(name = "author_id")
    private Long authorId;

    @Column(nullable = false)
    private String authorName;
    
    @Size(max=2000, message="Message too long")
    @NotBlank(message="Ticket message cannot be empty")
    private String messageText;

    private String attachmentName;
    private String attachmentType;
    private Long attachmentSize;
    private String attachmentPath;

    private LocalDateTime createdAt;    

    private Boolean read = false;

    public TicketMessageEntity(){
        this.createdAt = LocalDateTime.now();
    }
}
