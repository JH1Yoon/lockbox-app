package com.example.lockbox.common.service;

import com.example.lockbox.common.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlackListService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    // 블랙리스트에 토큰 추가
    public void addAccessTokenToBlackList(String token) {
        String cleanedToken = token.replace("Bearer ", "");

        redisTemplate.opsForValue().set(
                "AT:" + cleanedToken,
                cleanedToken,
                jwtUtil.getTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isAccessTokenBlackListed(String token) {
        String cleanedToken = token.replace("Bearer ", "");

        return redisTemplate.hasKey("AT:" + cleanedToken);
    }

    // 블랙리스트에서 토큰 삭제
    public void removeAccessTokenFromBlackList(String token) {
        String cleanedToken = token.replace("Bearer ", "");

        redisTemplate.delete("AT:" + cleanedToken);
    }
}