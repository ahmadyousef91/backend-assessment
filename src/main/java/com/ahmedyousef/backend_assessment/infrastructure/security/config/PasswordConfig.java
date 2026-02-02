package com.ahmedyousef.backend_assessment.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class PasswordConfig {

    private final SecurityProps props;

    @Bean
    public PasswordEncoder passwordEncoder() {
        var passwordConfig = props.getPasswordConfig();
        return new Argon2PasswordEncoder(
                passwordConfig.getSaltLength(),
                passwordConfig.getHashLength(),
                passwordConfig.getParallelism(),
                passwordConfig.getMemoryKb(),
                passwordConfig.getIterations()
        );
    }

}
