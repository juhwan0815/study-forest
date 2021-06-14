package com.study.authservice.model.user;

import com.study.authservice.model.common.KakaoProfile;
import lombok.Data;

@Data
public class UserLoginRequest {

    private Long kakaoId; // 카카오 Id;

    private String nickName; // 닉네임

    // TODO 이미지가 필수적으로 있는지 확인
    private String thumbnailImage; // 썸네일 이미지

    // TODO 이미지가 필수적으로 있는지 확인
    private String profileImage; // 프로필 이미지

    private String ageRange; // 나이대

    private String gender; // 성

    public static UserLoginRequest from(KakaoProfile kakaoProfile){
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.kakaoId = kakaoProfile.getId();
        userLoginRequest.nickName = kakaoProfile.getProperties().getNickname();
        userLoginRequest.thumbnailImage = kakaoProfile.getProperties().getThumbnail_image();
        userLoginRequest.profileImage = kakaoProfile.getProperties().getProfile_image();
        userLoginRequest.ageRange = kakaoProfile.getKakao_account().getAge_range();
        userLoginRequest.gender = kakaoProfile.getKakao_account().getGender();
        return userLoginRequest;
    }

}
