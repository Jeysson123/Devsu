package com.devsu.backend.web.controller;

import com.devsu.backend.web.dto.AuthRequest;
import com.devsu.backend.web.dto.AuthResponse;
import com.devsu.backend.web.dto.ResponseWrapper;
import com.devsu.backend.domain.factory.ResponseWrapperFactory;
import com.devsu.backend.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Provides authentication endpoints; generates JWT tokens for valid client credentials. */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AuthResponse>> generateToken(@RequestBody AuthRequest request) {
        String token = tokenProvider.generateToken(request.getClientId(), request.getPassword());
        long expiresIn = tokenProvider.getTokenExpirationTime();

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .build();

        return ResponseEntity.ok(ResponseWrapperFactory.successResponse(authResponse, HttpStatus.OK));
    }
}