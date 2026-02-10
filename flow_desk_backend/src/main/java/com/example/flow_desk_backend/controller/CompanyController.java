package com.example.flow_desk_backend.controller;

import java.util.List;

import com.example.flow_desk_backend.dto.ApiResponse;
import com.example.flow_desk_backend.dto.CompanyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    
    private final CompanyService companyService;

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies(){
        List<CompanyResponse> companies = companyService.getAllCompanies();
        ApiResponse<List<CompanyResponse>> response = new ApiResponse<>(
                true,
                companies.size() + " companies found",
                companies
        );

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @GetMapping("/get-company")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(){
        ApiResponse<CompanyResponse> response = new ApiResponse<>(
                true,
                "Company fetched successfully",
                companyService.getCompany()
        );

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
}
