package com.study.notificationservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id; // 회원 Id

    private String fcmToken; // 회원 FCM 토큰
}
