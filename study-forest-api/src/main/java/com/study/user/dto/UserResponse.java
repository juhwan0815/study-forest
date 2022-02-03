package com.study.user.dto;

import com.study.area.Area;
import com.study.area.dto.AreaResponse;
import com.study.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId; // 회원 ID

    private String nickName; // 닉네임

    private String imageUrl; // 이미지

    private String ageRange; // 나이대

    private String gender; // 성별

    private int distance; // 검색거리

    private String pushToken; // FCM 토큰

    private AreaResponse area;

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.userId = user.getId();
        userResponse.nickName = user.getNickName();
        userResponse.ageRange = user.getAgeRange();
        userResponse.gender = user.getGender();
        userResponse.distance = user.getDistance();
        userResponse.pushToken = user.getPushToken();
        userResponse.area = AreaResponse.from(user.getArea());
        return userResponse;
    }
}
