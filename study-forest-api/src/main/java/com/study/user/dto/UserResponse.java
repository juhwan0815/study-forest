package com.study.user.dto;

import com.study.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId; // 회원 ID

    private String nickName; // 닉네임

    private String imageUrl; // 이미지

    private String ageRange; // 나이대

    private String gender; // 성별

    private String pushToken; // FCM 토큰

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.userId = user.getId();
        userResponse.nickName = user.getNickName();
        userResponse.ageRange = user.getAgeRange();
        userResponse.gender = user.getGender();
        userResponse.pushToken = user.getPushToken();
        return userResponse;
    }
}
