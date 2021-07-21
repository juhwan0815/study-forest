package com.study.authservice.service;

import com.study.authservice.model.common.CreateTokenResult;

public interface AuthService {

    CreateTokenResult login(String kakaoToken,String fcmToken);

    String refresh(String refreshToken,Long userId);

    void delete(Long userId);
}
