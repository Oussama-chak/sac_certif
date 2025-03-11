package com.example.certif_app.services.security;

import com.example.certif_app.dto.request.SignUpRequest;
import com.example.certif_app.dto.request.SigninRequest;
import com.example.certif_app.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse SignUp(SignUpRequest request);
    JwtAuthenticationResponse Signin(SigninRequest request);
}
