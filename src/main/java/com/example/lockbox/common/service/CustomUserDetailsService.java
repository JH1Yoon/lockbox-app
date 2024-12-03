package com.example.lockbox.common.service;

import com.example.lockbox.common.config.JwtUtil;
import com.example.lockbox.common.dto.CustomUserDetails;
import com.example.lockbox.domain.user.entity.User;
import com.example.lockbox.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrThrow(email);

        return new CustomUserDetails(user);
    }
}