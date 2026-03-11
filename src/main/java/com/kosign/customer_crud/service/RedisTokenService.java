package com.kosign.customer_crud.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_PREFIX = "access:";
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void saveAccessToken(String token, String username, long ttlSecond){
        redisTemplate.opsForValue()
                .set(ACCESS_PREFIX + token,username,ttlSecond, TimeUnit.SECONDS);
    }

    // store refresh token
    public void saveRefreshToken(String token, String username, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(REFRESH_PREFIX + token, username, ttlSeconds, TimeUnit.SECONDS);
    }

    // validate access token exists in redis
    public boolean isAccessTokenValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ACCESS_PREFIX + token))
                && !isBlacklisted(token);
    }

    // validate refresh token exists in redis
    public boolean isRefreshTokenValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REFRESH_PREFIX + token));
    }

    // get username from refresh token
    public String getUsernameFromRefreshToken(String token) {
        return redisTemplate.opsForValue().get(REFRESH_PREFIX + token);
    }

    // delete both token when logout
    public void deleteTokens(String accessToken, String refreshToken) {
        redisTemplate.delete(ACCESS_PREFIX + accessToken);
        redisTemplate.delete(REFRESH_PREFIX + refreshToken);
    }

    // blacklist a token (immediate revoke)
    public void blacklistToken(String token, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + token, "revoked", ttlSeconds, TimeUnit.SECONDS);
    }

    // Check if token is blacklisted
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
