package com.example.flow_desk_backend.security;

import com.example.flow_desk_backend.service.LoginAttemptService;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String email){
        UserEntity user = userRepository.findByEmployeeEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(loginAttemptService.isLocked(email)){
            throw new LockedException("Account Locked");
        }

        return new CustomUserDetails(user);
    }
}
