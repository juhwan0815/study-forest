package com.study.userservice.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import com.study.userservice.domain.Image;
import com.study.userservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id; // 회원 Id

    private Long kakaoId; // 카카오 Id

    private String nickName; // 닉네임

    private Image image; // 이미지

    private String gender; // 성별

    private String ageRange; // 나이대

    private Integer numberOfStudyApply; // 스터디 신청 개수

    private Long locationId; // 지역 정보 ID

    public static UserResponse from(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.id = user.getId();
        userResponse.kakaoId = user.getKakaoId();
        userResponse.nickName = user.getNickName();
        userResponse.image = user.getImage();
        userResponse.gender = user.getGender();
        userResponse.ageRange = user.getAgeRange();
        userResponse.numberOfStudyApply = user.getNumberOfStudyApply();
        userResponse.locationId = user.getLocationId();
        return userResponse;
    }

}
