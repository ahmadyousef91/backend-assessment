package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.User;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_and_findByUsername_shouldWork() {
        UserRole role = UserRole.values()[0];

        User saved = userRepository.save(
                User.builder()
                        .username("ahmed")
                        .role(role)
                        .build()
        );

        Optional<User> loaded = userRepository.findByUsername("ahmed");

        assertThat(saved.getId()).isNotNull();
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(saved.getId());
        assertThat(loaded.get().getRole()).isEqualTo(role);
    }

    @Test
    void existsByUsername_shouldReturnTrueWhenExists() {
        UserRole role = UserRole.values()[0];

        userRepository.save(User.builder().username("user1").role(role).build());

        assertThat(userRepository.existsByUsername("user1")).isTrue();
        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }

    @Test
    void findByRole_shouldReturnUsersWithSameRole() {
        UserRole role = UserRole.values()[0];

        userRepository.save(User.builder().username("u1").role(role).build());
        userRepository.save(User.builder().username("u2").role(role).build());

        assertThat(userRepository.findByRole(role))
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("u1", "u2");
    }
}
