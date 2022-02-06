package com.study.service;

import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserFindRequest;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    void create(String kakaoToken);

    UserResponse findByKakaoId(Long kakaoId, String fcmToken);

    UserResponse findById(Long userId);

    void update(Long userId, UserUpdateRequest request);

    void updateArea(Long userId, Long areaId);

    void updateDistance(Long userId, UserUpdateDistanceRequest request);

    void addKeyword(Long userId, KeywordCreateRequest request);

    void deleteKeyword(Long userId, Long keywordId);

    List<KeywordResponse> findKeywordById(Long userId);

    List<UserResponse> findByIdIn(UserFindRequest request);

    List<UserResponse> findByKeywordContentContain(String studyName);

    String uploadImage(MultipartFile image);
}
