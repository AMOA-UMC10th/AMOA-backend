package com.AMOA.server.global.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private static final String REFRESH_PREFIX = "refresh:";
    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장
    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // 데이터 조회
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 존재 여부 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void saveRefreshToken(
            Long userId,
            String refreshToken,
            Duration duration
    ) {
        set(REFRESH_PREFIX + userId, refreshToken, duration);
    }

    public String getRefreshToken(Long userId) {
        Object value = get(REFRESH_PREFIX + userId);

        return value == null ? null : value.toString();
    }

    public boolean rotateRefreshToken(
            Long userId,
            String oldRefreshToken,
            String newRefreshToken,
            Duration expiration
    ) {
        String savedRefreshToken = getRefreshToken(userId);

        if (savedRefreshToken == null
                || !savedRefreshToken.equals(oldRefreshToken)) {
            return false;
        }

        saveRefreshToken(userId, newRefreshToken, expiration);

        return true;
    }

    public void deleteRefreshToken(Long userId) {
        delete(REFRESH_PREFIX + userId);
    }
}