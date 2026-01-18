package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="customer")
@Getter
@Setter
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;

    @Size(min=2, max=60, message="Customer name must be 2-60 characters")
    @NotBlank(message="Name cannot be empty")
    private String customerName;

    @NotBlank(message="Email cannot be empty")
    @Email(message="Invalid email format")
    private String email;

    @Pattern(regexp="^[0-9]{10}$", message="Phone must be 10 digits")
    private String phoneNumber;

    private Boolean isActive = true;

    @NotNull
    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private CompanyEntity company;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerEntity(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
