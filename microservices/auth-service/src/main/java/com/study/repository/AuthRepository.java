package com.study.repository;

public interface AuthRepository {

    void save(String userId, String refreshToken);
}
