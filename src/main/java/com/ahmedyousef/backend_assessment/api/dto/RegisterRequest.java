package com.ahmedyousef.backend_assessment.api.dto;

import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password) {
}