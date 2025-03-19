package com.alisimsek.taskmanagement.auth.service;

import com.alisimsek.taskmanagement.auth.controller.dto.request.LoginRequest;
import com.alisimsek.taskmanagement.auth.controller.dto.response.AuthResponse;
import com.alisimsek.taskmanagement.security.CustomUserDetailsService;
import com.alisimsek.taskmanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        return jwtUtil.getAuthResponse(userDetails);
    }
}
