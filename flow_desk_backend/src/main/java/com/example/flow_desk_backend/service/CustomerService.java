package com.example.flow_desk_backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.entity.CustomerEntity;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.repository.CustomerRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    public CustomerEntity addCustomer(CustomerEntity newCustomer){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        newCustomer.setCompany(loggedInUser.getCompany());

        CustomerEntity savedCustomer = customerRepository.save(newCustomer);
        return savedCustomer;
    }
}
