package com.study.userservice.service;

import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserResponse;

public interface UserService {

    UserResponse userLogin(UserLoginRequest request);

    void updateRefreshToken(RefreshTokenCreateMessage refreshTokenCreateMessage);
}
