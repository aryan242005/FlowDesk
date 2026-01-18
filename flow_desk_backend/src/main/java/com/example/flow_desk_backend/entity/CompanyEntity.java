package com.example.flow_desk_backend.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="company")
@Getter
@Setter
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long companyId;

    @Size(min=2, max=100, message="Company name must be between 2 to 100 characters")
    @NotBlank
    private String companyName;

    @NotBlank(message="Address cannot be blank")
    private String address;
    
    @NotBlank(message="Email cannot be blank")
    private String companyEmail;

    private LocalDateTime createdAt;

    public CompanyEntity(){
        this.createdAt = LocalDateTime.now();
    }
}