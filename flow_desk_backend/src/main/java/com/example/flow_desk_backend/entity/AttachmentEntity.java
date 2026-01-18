package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
@Table(name = "attachment")
@Getter
@Setter
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long attachmentId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private TicketEntity ticket;

    @NotBlank(message = "File name cannot be empty")
    @Size(max = 255)
    private String fileName;

    private String attachmentMessage;
    
    @NotBlank(message = "MIME type required")
    private String mimeType;

    @NotNull
    private byte[] fileData;

    private LocalDateTime createdAt;

    public AttachmentEntity() {
        this.createdAt = LocalDateTime.now();
    }
}
