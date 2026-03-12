package com.portfolio.backend.application;

import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.domain.PlatformUserRepository;
import com.portfolio.backend.domain.UseCase;
import com.portfolio.backend.domain.Exceptions.UserNotFound;

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
