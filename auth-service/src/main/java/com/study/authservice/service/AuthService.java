package com.study.authservice.service;

import com.study.authservice.model.CreateTokenResult;

public interface AuthService {

    CreateTokenResult login(String kakaoToken);

    CreateTokenResult refresh(String refreshToken);
}
