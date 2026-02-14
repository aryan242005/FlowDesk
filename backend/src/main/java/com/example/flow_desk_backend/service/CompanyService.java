package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.CompanyResponse;
import com.example.flow_desk_backend.entity.Role;
import com.example.flow_desk_backend.exception.AuthorizationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.exception.UserNotFoundException;
import com.example.flow_desk_backend.repository.CompanyRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;
import com.example.flow_desk_backend.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyResponse> getAllCompanies() {
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Check if current user is super admin
        if(!loggedInUser.getRole().equals(Role.SUPER_ADMIN)){
            throw new AuthorizationException("You cannot view all the companies");
        }

        List<CompanyEntity> companies = companyRepository.findAll();

        return companies.stream().map(this::mapToDto).toList();
    }

    public CompanyResponse getCompany(){
        // Fetch user
        UserEntity loggedInUser = getLoggedInUser();

        // Map and return company
        return mapToDto(loggedInUser.getCompany());
    }

    // Utility Functions

    public CompanyResponse mapToDto(CompanyEntity company){
        return new CompanyResponse(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getCompanyAddress(),
                company.getCompanyEmail()
        );
    }

    public UserEntity getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getUser();
    }
}
