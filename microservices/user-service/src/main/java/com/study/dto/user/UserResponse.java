package com.study.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.domain.User;
import com.study.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId; // 회원 ID

    private UserRole role; // 유저 권한

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String imageUrl; // 이미지

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long areaId; // 지역 Id

    private Integer distance; // 검색거리

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String fcmToken; // FCM 토큰

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getRole(), user.getNickName(),
                                user.getAgeRange(), user.getGender(), user.getImage().getImageUrl(),
                                user.getAreaId(), user.getDistance(), user.getFcmToken());
    }
}
