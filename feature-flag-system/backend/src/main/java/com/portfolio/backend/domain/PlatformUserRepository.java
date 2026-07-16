package com.portfolio.backend.domain;

import java.util.Optional;

public interface PlatformUserRepository {
    Optional<PlatformUser> findByUsername(String username);
    void register(PlatformUser user);
}
