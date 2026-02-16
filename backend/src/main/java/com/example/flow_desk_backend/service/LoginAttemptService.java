package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration LOCK_TTL = Duration.ofMinutes(15);
    private final StringRedisTemplate redis;

    public boolean isLocked(String email){
        return Boolean.TRUE.equals(redis.hasKey(lockKey(email)));
    }

    private String failKey(String email){
        return "login:fail:" + email;
    }

    private String lockKey(String email){
        return "login:lock:" + email;
    }

    public void onFailure(String email){
        Long attempts = redis.opsForValue().increment(failKey(email));

        if(attempts != null && attempts >= MAX_ATTEMPTS){
            redis.opsForValue().set(lockKey(email), "LOCKED", LOCK_TTL);
        }
    }

    public void onSuccess(String email){
        redis.delete(failKey(email));
        redis.delete(lockKey(email));
    }
}
