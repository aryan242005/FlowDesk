package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Size(min = 2, max = 120, message = "Title must be between 2 to 120 characters")
    @NotBlank
    @Column(nullable=false, length=120)
    private String title;

    @Size(min = 2, max = 500, message = "Description must be between 2 to 500 characters")
    @NotBlank
    private String ticketDescription;

    @NotNull
    @ManyToOne
    @JoinColumn(name="company_id")
    private CompanyEntity company;

    @NotNull
    @ManyToOne
    @JoinColumn(name="project_id")
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name="created_by", nullable=false)
    @NotNull
    private UserEntity createdBy;
    
    @ManyToOne
    @JoinColumn(name="assigned_user_id", nullable=true)
    private UserEntity assignedTo;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
