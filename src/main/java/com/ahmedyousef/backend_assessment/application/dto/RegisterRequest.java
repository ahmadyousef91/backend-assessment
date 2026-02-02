package com.ahmedyousef.backend_assessment.application.dto;

import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull UserRole role
) {
}