package com.study.userservice.service;

import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserProfileUpdateRequest;
import com.study.userservice.model.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse save(UserLoginRequest request);

    UserResponse profileUpdate(Long userId, MultipartFile image, UserProfileUpdateRequest request);

    UserResponse findById(Long userId);
}
