package com.ahmedyousef.backend_assessment.api.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}
