package com.example.certif_app.services.security.impl;

import com.example.certif_app.dto.request.SignUpRequest;
import com.example.certif_app.dto.request.SigninRequest;
import com.example.certif_app.dto.response.JwtAuthenticationResponse;
import com.example.certif_app.repos.UserRepository;
import com.example.certif_app.services.security.AuthenticationService;
import com.example.certif_app.services.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.certif_app.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    

    @Override
    public JwtAuthenticationResponse Signin(SigninRequest request)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByEmail(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid "));
        var jwt = jwtService.generateToken((UserDetails) user);
        return JwtAuthenticationResponse.builder().token(jwt).
                userId(user.getId()) // Set the userIdemail
                 // Set the role
                .build();

    }}