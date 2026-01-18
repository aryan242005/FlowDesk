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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class UserEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name="company_id")
    private CompanyEntity company;

    @Size(min=2, max=100, message="Employee name must be between 2 to 100 characters")
    @NotBlank(message="Name cannot be empty")
    private String employeeName;

    @Email(message="Invalid email format")
    @NotBlank(message="Email cannot be empty")
    private String employeeEmail;

    @Size(min=6, max=100, message="Password must be 6-100 characters")
    @NotBlank(message="Password cannot be empty")
    private String password;

    private String department;

    private String designation;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @Pattern(regexp="^[0-9]{10}$", message="Phone must be 10 digits")
    private String phoneNumber;

    @NotNull
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    public UserEntity(){
        this.createdAt = LocalDateTime.now(); 
    }
}
