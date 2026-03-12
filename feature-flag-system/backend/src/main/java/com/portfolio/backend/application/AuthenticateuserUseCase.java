package com.portfolio.backend.application;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.portfolio.backend.domain.Exceptions.UserNotFound;
import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.domain.PlatformUserRepository;
import com.portfolio.backend.domain.UseCase;
import com.portfolio.backend.infra.dto.LoginUserDto;

public class AuthenticateuserUseCase implements UseCase<LoginUserDto, PlatformUser> {
    private final PlatformUserRepository repository;
    private final AuthenticationManager authManager;

    public AuthenticateuserUseCase(PlatformUserRepository repository, AuthenticationManager authManager) {
        this.repository = repository;
        this.authManager = authManager;
    }

    @Override
    public PlatformUser run(LoginUserDto input) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword(), null)
        );

        return this.repository
            .findByUsername(input.getEmail())
            .orElseThrow(() -> new UserNotFound());
    }
    
}
