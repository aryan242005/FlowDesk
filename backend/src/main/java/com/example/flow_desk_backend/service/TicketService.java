package com.example.flow_desk_backend.service;

import java.util.EnumSet;
import java.util.List;

import com.example.flow_desk_backend.entity.*;
import com.example.flow_desk_backend.exception.*;
import com.example.flow_desk_backend.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.dto.EmployeeResponse;
import com.example.flow_desk_backend.dto.TicketResponse;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // Create Ticket
    public TicketResponse createTicket(Long projectId, String title, String description, TicketPriority priority) {
        // Find Project for which issue is to be raised
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        // Find current loggedInUser user
        UserEntity loggedInUser = validateAndGetCurrentUser(project, "You cannot create ticket in another company's project", "Only project members can add ticket");

        // Create ticket and assign data to it
        TicketEntity newTicket = new TicketEntity();
        newTicket.setTitle(title);
        newTicket.setTicketDescription(description);
        newTicket.setPriority(priority);

        // Set other details for new ticket
        newTicket.setCreatedBy(loggedInUser);
        newTicket.setProject(project);
        newTicket.setCompany(loggedInUser.getCompany());

        // Save new ticket
        TicketEntity savedTicket = ticketRepository.save(newTicket);

        return mapToDto(savedTicket);
    }

    public TicketResponse assignEmployee(Long employeeId, Long ticketId) {
        // Find ticket to which you want to assign employee
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        // Fetch loggedInUser
        UserEntity loggedInUser = validateAndGetCurrentUser(ticket.getProject(), "You cannot assign employee to tickets of another company's project", "Only project members can assign employee");

        // Find employee to whom ticket is to be assigned
        UserEntity employee = userRepository.findById(employeeId).orElseThrow(() -> new UserNotFoundException("Employee not found"));

        // Check if user is from same company and project
        if(!employee.getCompany().getCompanyId().equals(ticket.getCompany().getCompanyId())){
            throw new AuthorizationException("Only employee from this company can be assigned");
        }
        if(!ticket.getProject().getProjectMembers().contains(employee)){
            throw new AuthorizationException("Only employee from this project can be assigned");
        }

        // Set employee to ticket
        ticket.setAssignedTo(employee);
        ticketRepository.save(ticket);

        // Map employee to dto
        EmployeeResponse employeeResponse = new EmployeeResponse(
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

        // Map ticket to dto and return
        return new TicketResponse(
            ticket.getTicketId(),
            ticket.getTitle(),
            ticket.getTicketDescription(),
            ticket.getPriority().name(),
            ticket.getStatus().name(),
            ticket.getCreatedAt(),
            employeeResponse
        );
    }

    public TicketResponse updateTicketStatus(Long ticketId, TicketStatus status){
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        // Check if correct status is being updated
        if(!ticket.getStatus().canMoveTo(status)){
            throw new InvalidStatusException("Invalid status");
        }


        // Check if employee is assigned
        if(ticket.getAssignedTo() == null){
            throw new EmployeeNotAssignedException("Employee must be assigned to update status");
        }

        // Fetch loggedInUser
        UserEntity loggedInUser = validateAndGetCurrentUser(ticket.getProject(),"You cannot update tickets of another company's project", "Only project members can update ticket");

        // Check if manager or admin is closing the ticket
        if(status == TicketStatus.CLOSED && !EnumSet.of(Role.MANAGER, Role.COMPANY_ADMIN).contains(loggedInUser.getRole())){
            throw new AuthorizationException("Only manager or admin can close a ticket");
        }

        // Check if only assignee is updating status or not
        if(!ticket.getAssignedTo().getId().equals(loggedInUser.getId())){
            throw new AuthorizationException("Only assigned employee can update status");
        }

        // Update ticket status
        ticket.setStatus(status);
        TicketEntity updatedTicket = ticketRepository.save(ticket);

        return mapToDto(updatedTicket);
    }

    public TicketResponse getTicketById(Long ticketId) {
        // Fetch ticket
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        // Fetch loggedInUser
        UserEntity loggedInUser = validateAndGetCurrentUser(ticket.getProject(), "You cannot view tickets of another company's project", "Only project members can view ticket");

        EmployeeResponse employeeResponse = getEmployeeResponse(ticket);

        return new TicketResponse(
            ticket.getTicketId(),
            ticket.getTitle(),
            ticket.getTicketDescription(),
            ticket.getPriority().name(),
            ticket.getStatus().name(),
            ticket.getCreatedAt(),
            employeeResponse
        );
    }

    public Page<TicketResponse> getAllProjectTickets(Long companyId, Long projectId, int page, int size) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        // Validate current user
        UserEntity loggedInUser = validateAndGetCurrentUser(project, "You cannot view tickets of another company's project", "Only project members can view tickets");

        // Pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TicketEntity> tickets = ticketRepository.findByProject(project, pageable);
        return tickets.map(this::mapToDto);
    }

    // Utility Functions

    private static EmployeeResponse getEmployeeResponse(TicketEntity ticket) {
        if(ticket.getAssignedTo() == null) return null;
        UserEntity assignedEmployee = ticket.getAssignedTo();

        return new EmployeeResponse(
                assignedEmployee.getId(),
                assignedEmployee.getEmployeeName(),
                assignedEmployee.getEmployeeEmail(),
                assignedEmployee.getDepartment(),
                assignedEmployee.getDesignation(),
                assignedEmployee.getPhoneNumber(),
                assignedEmployee.getRole(),
                assignedEmployee.getIsActive(),
                assignedEmployee.getCreatedAt()
        );
    }

    public TicketResponse mapToDto(TicketEntity ticket){
        return new TicketResponse(
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getTicketDescription(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                getEmployeeResponse(ticket)
        );
    }

    public UserEntity validateAndGetCurrentUser(ProjectEntity project, String message1, String message2){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        // Check if project belongs to loggedInUser's company
        if(!project.getCompany().getCompanyId().equals(loggedInUser.getCompany().getCompanyId())){
            throw new AuthorizationException(message1);
        }

        // Check if current user is member of project or not
        if(!project.getProjectMembers().contains(loggedInUser)){
            throw new AuthorizationException(message2);
        }

        return loggedInUser;
    }
}
