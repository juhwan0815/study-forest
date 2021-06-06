package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 회원 Id

    @Column(unique = true)
    private Long kakaoId; // 카카오 Id

    private String nickName; // 닉네임

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    private String refreshToken; // Refresh 토큰

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 회원 상태

    @Enumerated(EnumType.STRING)
    private UserRole role; // 회원 권한

    public static User createUser(Long kakaoId, String nickName,
                                  String thumbnailImage, String profileImage,UserRole role){
        User user = new User();
        user.kakaoId = kakaoId;
        user.nickName = nickName;
        user.thumbnailImage = thumbnailImage;
        user.profileImage = profileImage;
        user.status = UserStatus.ACTIVE;
        user.role = role;
        return user;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void logout() {
        this.refreshToken = null;
    }
}
