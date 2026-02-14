package com.example.flow_desk_backend.repository;

import com.example.flow_desk_backend.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
