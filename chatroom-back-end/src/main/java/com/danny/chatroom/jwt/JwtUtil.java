package com.danny.chatroom.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("test-secret-key-modified-date-20260429".getBytes());
    private static final int EXPIRATION_TIME = 86400000;

    public String generateToken(String account) {
        Date issuedTime = new Date();
        Date expirationTime = new Date(issuedTime.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(account)
                .setIssuedAt(issuedTime)
                .setExpiration(expirationTime)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}