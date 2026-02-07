package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="ticket_comment")
@Getter
@Setter
public class TicketCommentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketCommentId;

    @NotNull
    @ManyToOne
    @JoinColumn(name="ticket_id", nullable = false)
    private TicketEntity ticket;

    @NotNull
    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name="created_by", nullable=false)
    private UserEntity createdBy;
    
    @Size(max=2000, message="Comment too long")
    @NotBlank(message="Ticket comment cannot be empty")
    private String commentText;

    private String attachmentName;
    private String attachmentType;
    private Long attachmentSize;
    private String attachmentPath;

    @CreationTimestamp
    private LocalDateTime createdAt;    

    private Boolean isRead = false;
}
