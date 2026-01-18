package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketId;

    @Size(min = 2, max = 120, message = "Title must be between 2 to 120 characters")
    @NotBlank
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
    @JoinColumn(name="customer_id")
    private CustomerEntity customer;
    
    @ManyToOne
    @JoinColumn(name="assigned_user_id", nullable=true)
    private UserEntity employee;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDateTime createdAt;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public TicketEntity() {
        this.createdAt = LocalDateTime.now();
    }
}
