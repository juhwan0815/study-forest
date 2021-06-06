package com.study.userservice.service;

import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserResponse;

public interface UserService {

    UserResponse save(UserLoginRequest request);

    void updateRefreshToken(RefreshTokenCreateMessage refreshTokenCreateMessage);

    UserResponse findWithRefreshTokenById(Long userId);

    void logout(LogoutMessage logoutMessage);


}
