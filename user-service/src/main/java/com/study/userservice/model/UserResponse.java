package com.study.userservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.domain.UserStatus;
import lombok.Data;

@Data
public class UserResponse {

    private Long id; // 회원 Id

    private Long kakaoId; // 카카오 Id

    private String nickName; // 닉네임

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String refreshToken; // refreshToken

    private String gender;

    private String ageRange;

    private UserStatus status; // 회원 상태

    private UserRole role; // 회원 권한

    public static UserResponse from(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.id = user.getId();
        userResponse.kakaoId = user.getKakaoId();
        userResponse.nickName = user.getNickName();
        userResponse.thumbnailImage = user.getThumbnailImage();
        userResponse.profileImage = user.getProfileImage();
        userResponse.gender = user.getGender();
        userResponse.ageRange = user.getAgeRange();
        userResponse.status = user.getStatus();
        userResponse.role = user.getRole();
        return userResponse;
    }

    public static UserResponse fromWithRefreshToken(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.id = user.getId();
        userResponse.kakaoId = user.getKakaoId();
        userResponse.nickName = user.getNickName();
        userResponse.thumbnailImage = user.getThumbnailImage();
        userResponse.profileImage = user.getProfileImage();
        userResponse.refreshToken = user.getRefreshToken();
        userResponse.gender = user.getGender();
        userResponse.ageRange = user.getAgeRange();
        userResponse.status = user.getStatus();
        userResponse.role = user.getRole();
        return userResponse;
    }
}
