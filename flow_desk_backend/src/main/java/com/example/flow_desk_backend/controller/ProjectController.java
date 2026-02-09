package com.example.flow_desk_backend.controller;

import com.example.flow_desk_backend.dto.AddProjectMemberRequest;
import com.example.flow_desk_backend.dto.ApiResponse;
import com.example.flow_desk_backend.dto.CreateProjectRequest;
import com.example.flow_desk_backend.dto.ProjectResponse;
import com.example.flow_desk_backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@RequestBody CreateProjectRequest request){
        ApiResponse<ProjectResponse> response = new ApiResponse<>(
                true,
                "Project created successfully",
                projectService.createProject(request)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> addProjectMember(@RequestBody AddProjectMemberRequest request){
        ApiResponse<ProjectResponse> response = new ApiResponse<>(
                true,
                "Project created successfully",
                projectService.addProjectMember(request)
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
