package com.ahmedyousef.backend_assessment.application.dto;

import com.ahmedyousef.backend_assessment.domain.enums.UserRole;

public record RegisterResponse(
        Long id,
        String username,
        UserRole role
) {}
