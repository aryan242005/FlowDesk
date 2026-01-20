package com.example.flow_desk_backend.service;

import java.util.List;

import com.example.flow_desk_backend.entity.CustomerEntity;
import com.example.flow_desk_backend.exception.*;
import com.example.flow_desk_backend.repository.CompanyRepository;
import com.example.flow_desk_backend.repository.CustomerRepository;
import com.example.flow_desk_backend.util.RedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.dto.EmployeeResponse;
import com.example.flow_desk_backend.dto.TicketResponse;
import com.example.flow_desk_backend.entity.TicketEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.entity.Status;
import com.example.flow_desk_backend.repository.TicketRepository;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final RedisService redisService;

    public TicketEntity addTicket(TicketEntity newTicket, Long companyId, String customerToken) {
        String customerEmail = redisService.get(customerToken);
        if (customerEmail == null) throw new InvalidTokenException();

        CustomerEntity customer = customerRepository.findByEmail(customerEmail);
        CustomerEntity savedCustomer;
        if(customer == null){
            CustomerEntity newCustomer = new CustomerEntity();
            newCustomer.setEmail(customerEmail);
            savedCustomer = customerRepository.save(newCustomer);
        }else{
            savedCustomer = customer;
        }
        if(savedCustomer.getCustomerName() == null || savedCustomer.getPhoneNumber() == null) throw new InformationMissingException("PLease enter name and phone");

        newTicket.setCompany(companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException("Company not found")));;
        newTicket.setCustomer(savedCustomer);

        return ticketRepository.save(newTicket);
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

    public TicketResponse updateTicketStatus(Long ticket_id, Status status){
        TicketEntity ticket = ticketRepository.findById(ticket_id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        if(ticket.getEmployee() == null){
            throw new EmployeeNotAssignedException("Empolyee must be assigned to update status");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        if(!ticket.getEmployee().getId().equals(loggedInUser.getId())){
            throw new AuthorizationException("Employee must be assigned to update status");
        }

        UserEntity employee = ticket.getEmployee();
        ticket.setStatus(status);

        TicketEntity updatedTicket = ticketRepository.save(ticket);

        return mapToDto(updatedTicket);
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

    public List<TicketResponse> getAllTickets() {
        List<TicketEntity> tickets = ticketRepository.findAll();
        return tickets.stream().map(this::mapToDto).toList();
    }

    public List<TicketResponse> getTicketsByCustomer(String customerToken){
        String customerEmail = redisService.get(customerToken);
        if (customerEmail == null) throw new InvalidTokenException();
        CustomerEntity customer = customerRepository.findByEmail(customerEmail);
        List<TicketEntity> tickets = ticketRepository.findByCustomerCustomerId(customer.getCustomerId());
        return tickets.stream().map(this::mapToDto).toList();
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
}
