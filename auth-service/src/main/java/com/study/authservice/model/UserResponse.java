package com.study.authservice.model;

import lombok.Data;

@Data
public class UserResponse {

    private Long id; // 회원 Id

    private Long kakaoId; // 카카오 Id

    private String nickName; // 닉네임

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    private String status; // 회원 상태

    private String role; // 회원 권한

}
