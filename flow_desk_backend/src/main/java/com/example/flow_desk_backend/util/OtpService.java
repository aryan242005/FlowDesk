package com.example.flow_desk_backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisService redisService;

    public static final Duration OTP_TTL = Duration.ofMinutes(5);

    public void saveOtp(String email, String hashedOtp){
        redisService.set("FORGOT_PWD_OTP:"+email, hashedOtp, OTP_TTL);
    }

    public String getOtp(String email){
        return redisService.get("FORGOT_PWD_OTP:"+email);
    }
}
