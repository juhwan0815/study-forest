package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 회원 ID

    private Long kakaoId; // 카카오 ID

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    @Enumerated(EnumType.STRING)
    private UserRole role; // 권한

    @Embedded
    private Image image; // 이미지 정보

    private String fcmToken; // fcmToken

    private Long areaId; // 지역 ID

    private Integer distance; // 검색 거리

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserKeyword> userKeywords = new ArrayList<>(); // 회원의 키워드들

    public static User createUser(Long kakaoId, String nickName, String ageRange, String gender, UserRole role) {
        User user = new User();
        user.kakaoId = kakaoId;
        user.nickName = nickName;
        user.ageRange = ageRange;
        user.gender = ageRange;
        user.role = role;
        user.distance = 3;
        return user;
    }

    public void changeImage(Image image) {
        this.image = image;
    }

    public void changeFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void changeProfile(String nickName) {
        this.nickName = nickName;
    }

    public void changeArea(Long areaId) {
        this.areaId = areaId;
    }

    public void changeDistance(Integer distance) {
        this.distance = distance;
    }
}
