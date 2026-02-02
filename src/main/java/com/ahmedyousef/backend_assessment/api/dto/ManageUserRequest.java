package com.ahmedyousef.backend_assessment.api.dto;

import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record ManageUserRequest(
        @NotNull Long userId,
        @NotNull UserRole userRole) {
}
