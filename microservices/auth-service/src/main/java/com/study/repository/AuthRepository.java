package com.study.repository;

public interface AuthRepository {

    void saveRefreshToken(String userId, String refreshToken);

    String findRefreshTokenByUserId(String userId);

    boolean getExpireIn7Days(String userId);

}
