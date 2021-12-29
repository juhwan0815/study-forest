package com.study.client;

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

    private String fcmToken; // FCM 토큰

}
