package com.ahmedyousef.backend_assessment.api.dto;

public record CachedPage<T>(
        java.util.List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        String sort
) {}
