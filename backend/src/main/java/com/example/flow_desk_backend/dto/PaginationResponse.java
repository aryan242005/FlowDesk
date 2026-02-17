package com.example.flow_desk_backend.dto;

public record PaginationResponse<T>(
        int page,
        int size,
        long totalElements,
        int totalPages,
        T data
) {}
