package com.study.service;

import com.study.dto.UserResponse;

public interface UserService {

    UserResponse create(String kakaoToken);

    UserResponse findByKakaoId(Long kakaoId, String fcmToken);
}
