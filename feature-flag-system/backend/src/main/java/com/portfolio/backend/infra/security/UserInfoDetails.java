package com.portfolio.backend.infra.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.portfolio.backend.domain.PlatformUserRepository;

@Service
public class UserInfoDetails implements UserDetailsService {
    private final PlatformUserRepository platformUserRepository;

    public UserInfoDetails(PlatformUserRepository platformUserRepository) {
        this.platformUserRepository = platformUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return platformUserRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + username));
    }
    
}
