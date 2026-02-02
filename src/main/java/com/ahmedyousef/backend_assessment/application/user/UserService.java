package com.ahmedyousef.backend_assessment.application.user;

import com.ahmedyousef.backend_assessment.api.dto.RegisterResponse;
import com.ahmedyousef.backend_assessment.domain.entity.User;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import com.ahmedyousef.backend_assessment.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(String username, String rawPassword, UserRole role) {

        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hash = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .username(username)
                .passwordHash(hash)
                .role(role)
                .build();
        User created = userRepo.save(user);

        return new RegisterResponse(created.getId(), created.getUsername(), created.getRole());
    }

    @Transactional
    public void manageUser(Long userId, UserRole role) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not exists: " + userId));

        user.setRole(role);
        userRepo.save(user);
    }
}
