package com.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final RedisTemplate redisTemplate;

    public void save(String userId, String refreshToken) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(userId, refreshToken, 30, TimeUnit.DAYS);
    }

}
