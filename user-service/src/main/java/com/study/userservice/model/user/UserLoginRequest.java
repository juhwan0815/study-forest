package com.study.userservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    @Positive(message = "카테고리 ID는 양수이어야 합니다.")
    private Long kakaoId; // 카카오 Id;

    @NotBlank(message = "스터디 이름은 필수입니다.")
    private String nickName; // 닉네임

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    @NotBlank(message = "나이는 필수입니다.")
    private String ageRange;

    @NotBlank(message = "성은 필수입니다.")
    private String gender;

    @NotBlank(message = "FCM토큰은 필수입니다.")
    private String fcmToken;
}
