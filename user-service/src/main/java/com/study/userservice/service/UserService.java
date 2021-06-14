package com.study.userservice.service;

import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.kafka.message.StudyJoinMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserProfileUpdateRequest;
import com.study.userservice.model.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse save(UserLoginRequest request);

    void updateRefreshToken(RefreshTokenCreateMessage refreshTokenCreateMessage);

    UserResponse findWithRefreshTokenById(Long userId);

    void logout(LogoutMessage logoutMessage);

    UserResponse profileUpdate(Long userId, MultipartFile image,UserProfileUpdateRequest request);

    void handleStudyJoin(StudyJoinMessage studyJoinMessage);
}
