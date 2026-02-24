package com.devsu.backend.infrastructure.security;

import com.devsu.backend.domain.factory.JwtTokenFactory;
import com.devsu.backend.web.config.MessageProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Base64;

/**
 * JwtTokenProvider generates, validates, and extracts client info from JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final MessageProvider messageProvider;

    private String getEncodedSecret() {
        return Base64.getEncoder().encodeToString(messageProvider.getJwtSecret().getBytes());
    }

    public String generateToken(String clientId, String password) {
        if (!messageProvider.getAuthClientId().equals(clientId) ||
                !messageProvider.getAuthPassword().equals(password)) {
            throw new RuntimeException("Credenciales de autenticación incorrectas");
        }

        return JwtTokenFactory.create(
                clientId,
                password,
                getEncodedSecret(),
                messageProvider.getJwtExpiration()
        );
    }

    public long getTokenExpirationTime() {
        return Long.parseLong(String.valueOf(messageProvider.getJwtExpiration()));
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getEncodedSecret()).parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token inválido o expirado: " + e.getMessage());
        }
    }

    public String getClientId(String token) {
        return Jwts.parser()
                .setSigningKey(getEncodedSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}