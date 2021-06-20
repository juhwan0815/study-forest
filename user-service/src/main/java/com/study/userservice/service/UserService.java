package com.study.userservice.service;

import com.study.userservice.model.user.UserFindRequest;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.model.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse create(UserLoginRequest request);

    UserResponse updateProfile(Long userId, MultipartFile image, UserUpdateProfileRequest request);

    UserResponse findById(Long userId);

    void delete(Long userId);

    List<UserResponse> findByIdIn(UserFindRequest request);

    void updateLocation(Long userId, Long locationId);
}
