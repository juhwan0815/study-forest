package com.study.service;

import com.study.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(String kakaoToken, String fcmToken);

    TokenResponse refresh(Long userId, String refreshToken);
}
