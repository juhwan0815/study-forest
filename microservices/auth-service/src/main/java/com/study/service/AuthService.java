package com.study.service;

import com.study.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(String kakaoToken, String fcmToken);

}
