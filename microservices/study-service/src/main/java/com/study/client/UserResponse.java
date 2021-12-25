package com.study.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId; // 회원 ID

    private String role; // 유저 권한

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    private String imageUrl; // 이미지

    private Long areaId; // 지역 Id

    private Integer distance; // 검색거리

    private String fcmToken; // FCM 토큰
}
