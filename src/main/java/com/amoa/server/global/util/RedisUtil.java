package com.amoa.server.global.util;

import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final DefaultRedisScript<Long> ROTATE_REFRESH_TOKEN_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local currentToken = redis.call('GET', KEYS[1])

                    if currentToken == ARGV[1] then
                        redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
                        return 1
                    end

                    return 0
                    """,
                    Long.class
            );

    private final StringRedisTemplate redisTemplate;

    // 데이터 저장
    public void set(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // 데이터 조회
    public String get(String key) {
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
        String key = REFRESH_PREFIX + userId;

        Long result = redisTemplate.execute(
                ROTATE_REFRESH_TOKEN_SCRIPT,
                Collections.singletonList(key),
                oldRefreshToken,
                newRefreshToken,
                String.valueOf(expiration.toMillis())
        );

        return Long.valueOf(1L).equals(result);
    }

    public void deleteRefreshToken(Long userId) {
        delete(REFRESH_PREFIX + userId);
    }

    public void saveBlackList(
            String accessToken,
            Duration duration
    ) {
        set(BLACKLIST_PREFIX + accessToken, "logout", duration);
    }

    public boolean isBlackListed(String accessToken) {
        return hasKey(BLACKLIST_PREFIX + accessToken);
    }
}