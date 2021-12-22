package com.study.service;

import com.study.dto.UserResponse;
import com.study.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse create(String kakaoToken);

    UserResponse findByKakaoId(Long kakaoId, String fcmToken);

    UserResponse findById(Long userId);

    UserResponse updateImage(Long userId, MultipartFile image);

    UserResponse updateProfile(Long userId, UserUpdateRequest request);

    void delete(Long userId);
}
