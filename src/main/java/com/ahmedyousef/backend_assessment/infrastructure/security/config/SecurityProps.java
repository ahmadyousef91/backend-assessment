package com.ahmedyousef.backend_assessment.infrastructure.security.config;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProps {

    @Valid
    @NotNull
    private Swagger swagger = new Swagger();

    @Valid
    @NotNull
    private PasswordProps passwordConfig;

    @Data
    public static class Swagger {
        private boolean enabled = true;
    }

    @Data
    public static class PasswordProps {
        @NotNull
        private String algorithm;
        @NotNull
        @Min(8)
        private Integer saltLength;
        @NotNull
        @Min(16)
        private Integer hashLength;
        @NotNull
        @Min(1)
        private Integer parallelism;
        @NotNull
        @Min(4096)
        private Integer memoryKb;
        @NotNull
        @Min(1)
        private Integer iterations;
    }
}
