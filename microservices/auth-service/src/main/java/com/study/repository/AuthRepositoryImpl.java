package com.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final RedisTemplate redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        valueOps.set(userId, refreshToken, 30, TimeUnit.DAYS);
    }

    @Override
    public String findRefreshTokenByUserId(String userId) {
        String refreshToken = valueOps.get(userId);
        if (refreshToken == null) {
            throw new RuntimeException();
        }
        return refreshToken;
    }

    @Override
    public boolean getExpireIn7Days(String userId) {
        Long expireDay = redisTemplate.getExpire(userId, TimeUnit.DAYS);
        return (expireDay < 7) ? true : false;
    }

}
