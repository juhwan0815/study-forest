package com.study.userservice.model;

import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.domain.UserStatus;
import lombok.Data;

@Data
public class UserResponse {

    private Long id; // 회원 Id

    private Long kakaoId; // 카카오 Id

    private String nickName;

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    private UserStatus status; // 회원 상태

    private UserRole role; // 회원 권한

    public static UserResponse from(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.id = user.getId();
        userResponse.kakaoId = user.getKakaoId();
        userResponse.nickName = user.getNickName();
        userResponse.thumbnailImage = user.getThumbnailImage();
        userResponse.profileImage = user.getProfileImage();
        userResponse.status = user.getStatus();
        userResponse.role = user.getRole();
        return userResponse;
    }
}
