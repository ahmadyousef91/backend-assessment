package com.ahmedyousef.backend_assessment.infrastructure.security.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "security.jwt")
@RequiredArgsConstructor
public class JwtProps {

    @Min(60)
    private long accessTtlSeconds = 900;

    @NotBlank
    private String hmacSecretBase64;
}
