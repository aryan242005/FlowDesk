package com.example.flow_desk_backend.repository;

import com.example.flow_desk_backend.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flow_desk_backend.entity.TicketEntity;

import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findByProject(ProjectEntity projectId);
}
