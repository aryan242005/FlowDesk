package com.example.flow_desk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flow_desk_backend.entity.CompanyEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    
}
