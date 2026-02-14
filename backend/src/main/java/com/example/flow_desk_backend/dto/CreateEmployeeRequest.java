package com.example.flow_desk_backend.dto;

import com.example.flow_desk_backend.entity.UserEntity;

public record CreateEmployeeRequest (
    UserEntity employee
){}
