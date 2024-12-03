package com.example.lockbox.common.config;

import com.example.lockbox.common.exception.NotFoundException;
import com.example.lockbox.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String email, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .claim("userRole", userRole)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(String email, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .claim("userRole", userRole)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public String invalidToken(String token) {
        Date now = new Date();

        Claims claims = extractClaims(token);
        return Jwts.builder()
                .setSubject(claims.getSubject())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(now)
                .signWith(key, signatureAlgorithm)
                .compact();
    }


    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()).trim();
        }
        throw new NotFoundException("토큰을 찾을 수 없습니다");
    }

    public Long getTokenExpirationTime() {
        return TOKEN_TIME;
    }

    public Claims extractClaims(String token) {
        try {
            if (token.startsWith(BEARER_PREFIX)) {
                token = token.substring(BEARER_PREFIX.length());
            }

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}


