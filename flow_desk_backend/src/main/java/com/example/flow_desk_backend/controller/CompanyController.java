package com.example.flow_desk_backend.controller;

import java.util.List;

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
    public ResponseEntity<List<CompanyEntity>> getAllCompanies(){
        List<CompanyEntity> companies = companyService.getAllCompanies();

        return ResponseEntity.ok(companies);
    }

    @GetMapping("/get-company")
    public ResponseEntity<CompanyEntity> getCompany(){
        CompanyEntity company = companyService.getCompany();

        return ResponseEntity.ok(company);
    }
}
