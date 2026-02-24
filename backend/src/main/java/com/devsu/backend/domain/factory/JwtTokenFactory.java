package com.devsu.backend.domain.factory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

/**
 * JwtTokenFactory generates JWT tokens for clients with a specified expiration.
 */
public class JwtTokenFactory {

    public static String create(String clientId, String password, String secret, long expiration) {
        return Jwts.builder()
                .setSubject(clientId)
                .claim("pwd", password)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}