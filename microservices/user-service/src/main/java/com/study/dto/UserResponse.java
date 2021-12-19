package com.study.dto;

import com.study.domain.User;
import com.study.repository.UserRepository;
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

    private String imageUrl; // 이미지

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickName(), user.getAgeRange(), user.getImage().getImageUrl());
    }
}
