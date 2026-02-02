package com.ahmedyousef.backend_assessment.infrastructure.security.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public record AppUserPrincipal(
        Long userId,
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities

) implements UserDetails {


    public AppUserPrincipal {
        // Defensive copy to avoid exposing mutable reference
        authorities = authorities == null
                ? Collections.emptyList()
                : List.copyOf(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}