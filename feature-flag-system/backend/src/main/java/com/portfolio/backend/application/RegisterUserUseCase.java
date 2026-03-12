package com.portfolio.backend.application;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.domain.PlatformUserRepository;
import com.portfolio.backend.domain.UseCase;
import com.portfolio.backend.infra.dto.RegisterUserDto;

public class RegisterUserUseCase implements UseCase<RegisterUserDto, Void> {

    private final PlatformUserRepository repository;
    private final PasswordEncoder encoder;

    public RegisterUserUseCase(PlatformUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public Void run(RegisterUserDto user) {
        PlatformUser platformUser = new PlatformUser(
            UUID.randomUUID(),
            user.getEmail(),
            encoder.encode(user.getPassword())
        );

        this.repository.register(platformUser);

        return null;
    }
    
}
