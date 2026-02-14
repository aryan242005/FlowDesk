package com.example.flow_desk_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String key, int limit, int ttlSeconds){
        Long count = redisTemplate.opsForValue().increment(key);

        if(count == 1){
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        }

        return count <= limit;
    }
}
