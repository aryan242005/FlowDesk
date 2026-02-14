package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.AddProjectMemberRequest;
import com.example.flow_desk_backend.dto.CreateProjectRequest;
import com.example.flow_desk_backend.dto.EmployeeResponse;
import com.example.flow_desk_backend.dto.ProjectResponse;
import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.entity.ProjectEntity;
import com.example.flow_desk_backend.entity.Role;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.exception.AuthorizationException;
import com.example.flow_desk_backend.exception.CompanyNotFoundException;
import com.example.flow_desk_backend.exception.ProjectNotFoundException;
import com.example.flow_desk_backend.exception.UserNotFoundException;
import com.example.flow_desk_backend.repository.CompanyRepository;
import com.example.flow_desk_backend.repository.ProjectRepository;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public ProjectResponse createProject(CreateProjectRequest request){
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Fetch company
        CompanyEntity company = companyRepository.findById(request.companyId()).orElseThrow(() -> new CompanyNotFoundException("Company with id " + request.companyId() + " not found"));

        // Check if user is authorized to create project
        if(!loggedInUser.getRole().equals(Role.MANAGER) && !loggedInUser.getRole().equals(Role.COMPANY_ADMIN)){
            throw new AuthorizationException("Only owner and manager can create new project");
        }

        // Check if user is creating project for his company
        if(!loggedInUser.getCompany().getCompanyId().equals(company.getCompanyId())){
            throw new AuthorizationException("You cannot create project in other company");
        }

        // Create new project
        ProjectEntity newProject = new ProjectEntity();

        newProject.setProjectName(request.projectName());
        newProject.setProjectDescription(request.projectDescription());
        newProject.setProjectCode(request.projectCode());

        // Add manager by default
        if(newProject.getProjectMembers() == null){
            newProject.setProjectMembers(new ArrayList<>());
        }
        newProject.getProjectMembers().add(loggedInUser);

        // Save project
        ProjectEntity savedProject = projectRepository.save(newProject);

        return mapProjectToDto(savedProject);
    }

    public ProjectResponse addProjectMember(AddProjectMemberRequest request){
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Fetch project
        ProjectEntity project = projectRepository.findById(request.projectId()).orElseThrow(() -> new ProjectNotFoundException("Project with id " + request.projectId() + " not found"));

        // Check if user is authorized to create project
        if(!loggedInUser.getRole().equals(Role.MANAGER) && !loggedInUser.getRole().equals(Role.COMPANY_ADMIN)){
            throw new AuthorizationException("Only owner and manager can create new project");
        }

        // Fetch user to add in project
        UserEntity user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException("User with id " + request.userId() + " not found"));
        if(!user.getCompany().getCompanyId().equals(project.getCompany().getCompanyId())){
            throw new AuthorizationException("You can only add members from same company");
        }

        project.getProjectMembers().add(user);

        ProjectEntity savedProject = projectRepository.save(project);

        return mapProjectToDto(savedProject);
    }

    // Utility Functions

    public ProjectResponse mapProjectToDto(ProjectEntity project){
        return new ProjectResponse(
            project.getProjectId(),
            project.getProjectName(),
            project.getProjectDescription(),
            project.getProjectCode(),
            mapToDto(project.getCreatedBy()),
            project.getProjectMembers().stream().map(this::mapToDto).toList(),
            project.getStatus(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }

    public EmployeeResponse mapToDto(UserEntity employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeName(),
                employee.getEmployeeEmail(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getPhoneNumber(),
                employee.getRole(),
                employee.getIsActive(),
                employee.getCreatedAt()
        );
    }

    public UserEntity getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getUser();
    }
}
