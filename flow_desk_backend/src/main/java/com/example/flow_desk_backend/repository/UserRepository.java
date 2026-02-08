package com.example.flow_desk_backend.repository;

import java.util.List;
import java.util.Optional;

import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flow_desk_backend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByEmployeeEmail(String email);
    List<UserEntity> findByCompany(CompanyEntity company);
    List<UserEntity> findByCompanyAndRoleIn(CompanyEntity company, List<Role> roles);
    List<UserEntity> findByProjectId(Long projectId);
}
