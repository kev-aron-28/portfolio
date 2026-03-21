package com.portfolio.backend.application;

import org.springframework.stereotype.Service;

import com.portfolio.backend.domain.Exceptions.UserNotFound;
import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.domain.PlatformUserRepository;
import com.portfolio.backend.domain.UseCase;

@Service
public class FindUserByEmailUseCase implements UseCase<String, PlatformUser>{
    private final PlatformUserRepository repository;

    public FindUserByEmailUseCase(PlatformUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlatformUser run(String username) {
        return this
            .repository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFound());
    }

}
