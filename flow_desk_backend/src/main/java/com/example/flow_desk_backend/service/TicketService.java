package com.example.flow_desk_backend.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.dto.EmployeeResponse;
import com.example.flow_desk_backend.dto.TicketResponse;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.entity.Status;
import com.example.flow_desk_backend.exception.TicketNotFoundException;
import com.example.flow_desk_backend.exception.UserNotFoundException;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketEntity addTicket(TicketEntity newTicket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        newTicket.setCompany(loggedInUser.getCompany());

        TicketEntity savedTicket = ticketRepository.save(newTicket);
        return savedTicket;
    }

    public TicketResponse assignEmployee(Long employee_id, Long ticket_id) {
        TicketEntity ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        UserEntity employee = userRepository.findById(employee_id).orElseThrow(() -> new UserNotFoundException("Employee not found"));

        ticket.setEmployee(employee);
        ticketRepository.save(ticket);

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

        TicketResponse ticketResponse = new TicketResponse(
            ticket.getTicketId(),
            ticket.getTitle(),
            ticket.getTicketDescription(),
            ticket.getPriority().name(),
            ticket.getStatus().name(),
            ticket.getCreatedAt(),
            employeeResponse
        );

        return ticketResponse;
    }

    public TicketResponse updateTicketStatus(Long ticket_id, Status status){
        TicketEntity ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        UserEntity employee = ticket.getEmployee();
        ticket.setStatus(status);

        TicketEntity updatedTicket = ticketRepository.save(ticket);

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

        TicketResponse ticketResponse = new TicketResponse(
            updatedTicket.getTicketId(),
            updatedTicket.getTitle(),
            updatedTicket.getTicketDescription(),
            updatedTicket.getPriority().name(),
            updatedTicket.getStatus().name(),
            updatedTicket.getCreatedAt(),
            employeeResponse
        );

        return ticketResponse;
    }

    public TicketResponse getTicketById(Long ticket_id) {
        TicketEntity ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
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

    private static EmployeeResponse getEmployeeResponse(TicketEntity ticket) {
        UserEntity assignedEmployee = ticket.getEmployee();

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

    public List<TicketEntity> getAllTickets() {
        List<TicketEntity> tickets = ticketRepository.findAll();
        return tickets;
    }
}
