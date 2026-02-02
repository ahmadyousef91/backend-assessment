package com.ahmedyousef.backend_assessment.api;

import com.ahmedyousef.backend_assessment.application.dto.LoginRequest;
import com.ahmedyousef.backend_assessment.application.dto.RegisterRequest;
import com.ahmedyousef.backend_assessment.application.dto.RegisterResponse;
import com.ahmedyousef.backend_assessment.application.dto.TokenResponse;
import com.ahmedyousef.backend_assessment.application.user.UserRegistrationService;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import com.ahmedyousef.backend_assessment.infrastructure.security.auth.AppUserPrincipal;
import com.ahmedyousef.backend_assessment.infrastructure.security.auth.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenService tokenService;
    private final UserRegistrationService registrationService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        AppUserPrincipal principal = (AppUserPrincipal) auth.getPrincipal();
        String jwt = tokenService.generateAccessToken(principal);

        return new TokenResponse(jwt, "Bearer", tokenService.accessTtlSeconds());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody @Valid RegisterRequest req) {
        RegisterResponse registerResponse = registrationService
                .register(req.username(), req.password(), UserRole.USER);
        return new RegisterResponse(registerResponse.id(), registerResponse.username(),
                registerResponse.role());
    }

}