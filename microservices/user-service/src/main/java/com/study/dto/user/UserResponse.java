package com.study.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import com.study.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId; // 회원 ID

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    private String imageUrl; // 이미지

    private Long areaId; // 지역 Id

    private Integer distance; // 검색거리

    private String fcmToken; // FCM 토큰

    @QueryProjection
    public UserResponse(User user) {
        this.userId = user.getId();
        this.nickName = user.getNickName();
        this.ageRange = user.getAgeRange();
        this.gender = user.getGender();
        this.imageUrl = user.getImageUrl();
        this.areaId = user.getAreaId();
        this.distance = user.getDistance();
        this.fcmToken = user.getFcmToken();
    }

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.userId = user.getId();
        userResponse.nickName = user.getNickName();
        userResponse.ageRange = user.getAgeRange();
        userResponse.gender = user.getGender();
        userResponse.imageUrl = user.getImageUrl();
        userResponse.areaId = user.getAreaId();
        userResponse.distance = user.getDistance();
        userResponse.fcmToken = user.getFcmToken();
        return userResponse;
    }
}
