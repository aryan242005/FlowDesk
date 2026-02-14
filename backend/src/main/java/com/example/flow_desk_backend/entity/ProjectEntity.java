package com.example.flow_desk_backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="projects")
@Getter
@Setter
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Size(min=5, max=20, message = "Project name must be between 5 to 20 characters")
    @NotBlank(message="Project name cannot be empty")
    @Column(nullable = false, length=20)
    private String projectName;

    @Size(min=10, max=100, message = "Project description must be between 10 to 100 characters")
    @NotBlank(message="Project description cannot be empty")
    private String projectDescription;

    @Column(unique=true, nullable = false)
    private String projectCode;

    @ManyToOne
    @JoinColumn(name="company_id", nullable = false)
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name="created_by", nullable = false)
    private UserEntity createdBy;

    @ManyToMany
    @JoinTable(
            name="project_members",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private List<UserEntity> projectMembers;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
